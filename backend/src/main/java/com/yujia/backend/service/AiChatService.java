package com.yujia.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.dto.ai.StaffAiChatRequest;
import com.yujia.backend.dto.ai.UserAiChatRequest;
import com.yujia.backend.vo.AiChatAnswerVO;
import com.yujia.backend.vo.AiChatReferenceVO;
import com.yujia.backend.vo.DashboardStatsVO;
import com.yujia.backend.vo.ProductBatchVO;
import com.yujia.backend.vo.RiskBatchVO;
import com.yujia.backend.vo.RiskOverviewVO;
import com.yujia.backend.vo.TraceDetailVO;
import com.yujia.backend.vo.TraceSummaryVO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private static final String STAFF_MODE = "STAFF";
    private static final String USER_MODE = "USER";

    private final ObjectMapper objectMapper;
    private final DashboardService dashboardService;
    private final RiskOverviewService riskOverviewService;
    private final BatchArchiveService batchArchiveService;
    private final BatchInsightService batchInsightService;
    private final ProductBatchService productBatchService;
    private final TraceCodeService traceCodeService;
    private final TraceSummaryService traceSummaryService;

    @Value("${app.ai.chat.enabled:true}")
    private boolean enabled;

    @Value("${app.ai.chat.api-key:}")
    private String apiKey;

    @Value("${app.ai.chat.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${app.ai.chat.model:deepseek-chat}")
    private String model;

    @Value("${app.ai.chat.temperature:0.1}")
    private double temperature;

    @Value("${app.ai.chat.connect-timeout-ms:3000}")
    private int connectTimeoutMs;

    @Value("${app.ai.chat.read-timeout-ms:6000}")
    private int readTimeoutMs;

    @PostConstruct
    public void validateConfig() {
        if (!enabled) {
            log.warn("AI chat service is disabled.");
            return;
        }
        if (apiKey == null || apiKey.isBlank()) {
            log.error("AI chat service is enabled but DEEPSEEK_API_KEY is missing.");
            return;
        }
        log.info("AI chat service is enabled. provider=DeepSeek, model={}, baseUrl={}", model, baseUrl);
    }

    public AiChatAnswerVO answerForStaff(StaffAiChatRequest request) {
        ensureAiReady();

        String question = requireQuestion(request.getQuestion());
        StaffContextBundle contextBundle = buildStaffContextBundle(request.getBatchId());
        String userPrompt = buildStaffUserPrompt(question, contextBundle.context(), request.getBatchId());
        String answer = askModel(buildStaffSystemPrompt(), userPrompt);

        return AiChatAnswerVO.builder()
                .mode(STAFF_MODE)
                .contextTitle(request.getBatchId() == null ? "当前企业经营数据" : "当前聚焦批次数据")
                .answer(answer)
                .references(contextBundle.references())
                .build();
    }

    public AiChatAnswerVO answerForUser(UserAiChatRequest request) {
        String question = requireQuestion(request.getQuestion());
        TraceDetailVO detail = traceCodeService.getTraceSnapshot(request.getTraceId(), request.getSign());
        TraceSummaryVO summary = traceSummaryService.summary(request.getTraceId(), request.getSign());
        String context = buildUserContext(detail, summary);
        List<AiChatReferenceVO> references = buildUserReferences(detail, summary);
        String answer = canUseAi()
                ? askModelOrFallback(buildUserSystemPrompt(), buildUserPrompt(question, context), question, detail,
                        summary)
                : buildUserFallbackAnswer(question, detail, summary);

        return AiChatAnswerVO.builder()
                .mode(USER_MODE)
                .contextTitle("当前商品公开溯源信息")
                .answer(answer)
                .references(references)
                .build();
    }

    private void ensureAiReady() {
        if (!enabled) {
            throw new BusinessException(503, "AI 问答功能未开启");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException(503, "AI 问答密钥未配置");
        }
    }

    private boolean canUseAi() {
        return enabled && apiKey != null && !apiKey.isBlank();
    }

    private String requireQuestion(String question) {
        String value = question == null ? "" : question.trim();
        if (value.isEmpty()) {
            throw new BusinessException("请输入问题内容");
        }
        return value;
    }

    private StaffContextBundle buildStaffContextBundle(Long batchId) {
        if (batchId != null) {
            return buildFocusedBatchContext(batchId);
        }
        return buildCompanyWideContext();
    }

    private StaffContextBundle buildCompanyWideContext() {
        DashboardStatsVO dashboard = dashboardService.stats();
        RiskOverviewVO risk = riskOverviewService.overview();
        StringBuilder builder = new StringBuilder();

        builder.append("【企业经营概览】\n")
                .append("基地数=").append(dashboard.getBaseCount()).append('\n')
                .append("批次数=").append(dashboard.getBatchCount()).append('\n')
                .append("溯源码数=").append(dashboard.getTraceCodeCount()).append('\n')
                .append("生产记录数=").append(dashboard.getProductionRecordCount()).append('\n')
                .append("质检报告数=").append(dashboard.getInspectionReportCount()).append('\n')
                .append("进行中召回数=").append(dashboard.getActiveRecallCount()).append('\n')
                .append("待处理任务数=").append(risk.getOpenTaskCount()).append('\n')
                .append("高风险批次数=").append(risk.getHighRiskBatchCount()).append('\n')
                .append("待处理反馈数=").append(risk.getPendingFeedbackCount()).append('\n')
                .append("异常扫码数=").append(risk.getAbnormalScanCount()).append('\n')
                .append("平均完整度=").append(risk.getAverageCompletenessScore()).append("%\n");

        if (risk.getTopRiskBatches() != null && !risk.getTopRiskBatches().isEmpty()) {
            builder.append("【高风险批次 TOP】\n");
            for (RiskBatchVO item : risk.getTopRiskBatches().stream().limit(5).toList()) {
                builder.append("- 批次=").append(safeText(item.getBatchCode()))
                        .append("，产品=").append(safeText(item.getProductName()))
                        .append("，风险分=").append(item.getRiskScore())
                        .append("，完整度=").append(item.getCompletenessScore()).append("%")
                        .append("，等级=").append(safeText(item.getRiskLevel()))
                        .append("，摘要=").append(safeText(item.getSummary()))
                        .append('\n');
            }
        }

        List<AiChatReferenceVO> references = new ArrayList<>();
        references.add(reference("summary", "企业仪表盘统计", null));
        references.add(reference("summary", "风险中心概览", null));
        if (risk.getTopRiskBatches() != null) {
            risk.getTopRiskBatches().stream()
                    .limit(3)
                    .forEach(batch -> references.add(reference(
                            "batch_archive",
                            "高风险批次 " + safeText(batch.getBatchCode()),
                            batch.getBatchId())));
        }
        return new StaffContextBundle(builder.toString(), references);
    }

    private StaffContextBundle buildFocusedBatchContext(Long batchId) {
        DashboardStatsVO dashboard = dashboardService.stats();
        ProductBatchVO batch = productBatchService.detail(batchId);
        var archive = batchArchiveService.archive(batchId);
        var insight = batchInsightService.insight(archive);
        RiskOverviewVO risk = riskOverviewService.overview();

        StringBuilder builder = new StringBuilder();
        builder.append("【回答范围约束】\n")
                .append("当前问题已明确聚焦到该批次，回答必须优先围绕这个批次展开，不要转成整个公司的泛化分析。\n")
                .append("只有在解释风险优先级或资源安排时，才可以少量引用企业整体概览作为补充。\n")
                .append("【企业概览补充】\n")
                .append("企业批次数=").append(dashboard.getBatchCount()).append('\n')
                .append("企业待处理任务数=").append(risk.getOpenTaskCount()).append('\n')
                .append("企业待处理反馈数=").append(risk.getPendingFeedbackCount()).append('\n')
                .append("【当前聚焦批次】\n")
                .append("批次编号=").append(safeText(batch.getBatchCode())).append('\n')
                .append("产品名称=").append(safeText(batch.getProductName())).append('\n')
                .append("产品品类=").append(safeText(batch.getProductCategory())).append('\n')
                .append("基地名称=").append(safeText(batch.getBaseName())).append('\n')
                .append("召回状态=").append(batch.getRecallStatus() != null && batch.getRecallStatus() == 1 ? "召回中" : "正常")
                .append('\n')
                .append("完整度=").append(insight.getCompletenessScore()).append("%\n")
                .append("风险分=").append(insight.getRiskScore()).append('\n')
                .append("风险等级=").append(safeText(insight.getRiskLevel())).append('\n')
                .append("批次摘要=").append(safeText(insight.getAiSummary())).append('\n')
                .append("生产记录数=").append(archive.getProductionRecords().size()).append('\n')
                .append("质检报告数=").append(archive.getInspectionReports().size()).append('\n')
                .append("物流节点数=").append(archive.getLogisticsRecords().size()).append('\n')
                .append("用户反馈数=").append(archive.getFeedbackCount()).append('\n')
                .append("高优反馈数=").append(archive.getHighPriorityFeedbackCount()).append('\n')
                .append("异常扫码数=").append(archive.getAbnormalScanCount()).append('\n');

        if (insight.getWarnings() != null && !insight.getWarnings().isEmpty()) {
            builder.append("风险提醒=").append(String.join("；", insight.getWarnings())).append('\n');
        }
        if (insight.getNextActions() != null && !insight.getNextActions().isEmpty()) {
            builder.append("系统建议=").append(String.join("；", insight.getNextActions())).append('\n');
        }

        List<AiChatReferenceVO> references = new ArrayList<>();
        references.add(reference("batch_archive", "批次 " + safeText(batch.getBatchCode()) + " 智能档案", batchId));
        references.add(reference("batch_archive", "批次 " + safeText(batch.getBatchCode()) + " 风险洞察", batchId));
        if (archive.getInspectionReports() != null && !archive.getInspectionReports().isEmpty()) {
            references.add(reference("batch_archive", "批次 " + safeText(batch.getBatchCode()) + " 质检报告", batchId));
        }
        if (archive.getFeedbackCount() > 0) {
            references.add(reference("batch_archive", "批次 " + safeText(batch.getBatchCode()) + " 用户反馈", batchId));
        }
        return new StaffContextBundle(builder.toString(), references);
    }

    private String buildUserContext(TraceDetailVO detail, TraceSummaryVO summary) {
        StringBuilder builder = new StringBuilder();
        builder.append("【当前商品公开信息】\n")
                .append("产品名称=").append(safeText(detail.getBatchInfo().getProductName())).append('\n')
                .append("批次编号=").append(safeText(detail.getBatchInfo().getBatchCode())).append('\n')
                .append("产品品类=").append(safeText(detail.getBatchInfo().getProductCategory())).append('\n')
                .append("种植日期=").append(safeText(detail.getBatchInfo().getPlantingDate())).append('\n')
                .append("实际采收=").append(safeText(detail.getBatchInfo().getActualHarvestDate())).append('\n')
                .append("基地名称=").append(safeText(detail.getBaseInfo().getBaseName())).append('\n')
                .append("基地负责人=").append(safeText(detail.getBaseInfo().getManagerName())).append('\n')
                .append("生产记录数=").append(sizeOf(detail.getProductionRecords())).append('\n')
                .append("质检报告数=").append(sizeOf(detail.getInspectionReports())).append('\n')
                .append("物流节点数=").append(sizeOf(detail.getLogisticsRecords())).append('\n')
                .append("召回预警=").append(detail.isRecallWarning() ? "是" : "否").append('\n');

        if (detail.getVerifyInfo() != null) {
            builder.append("验真结果=").append(safeText(detail.getVerifyInfo().getVerifyMessage())).append('\n')
                    .append("扫码次数=").append(detail.getVerifyInfo().getScanCount()).append('\n')
                    .append("是否首次扫码=").append(detail.getVerifyInfo().isFirstScan() ? "是" : "否").append('\n');
            if (detail.getVerifyInfo().getRiskMessage() != null && !detail.getVerifyInfo().getRiskMessage().isBlank()) {
                builder.append("验真风险提示=").append(detail.getVerifyInfo().getRiskMessage()).append('\n');
            }
        }

        if (detail.getRecallRecord() != null) {
            builder.append("召回原因=").append(safeText(detail.getRecallRecord().getReason())).append('\n')
                    .append("召回级别=").append(detail.getRecallRecord().getRecallLevel()).append('\n');
        }

        builder.append("【系统摘要】\n")
                .append("标题=").append(safeText(summary.getSummaryTitle())).append('\n')
                .append("摘要=").append(safeText(summary.getSummaryText())).append('\n')
                .append("可信度=").append(summary.getTrustScore()).append('\n')
                .append("风险提示=").append(joinOrDash(summary.getRiskTips())).append('\n')
                .append("行动建议=").append(joinOrDash(summary.getActionTips())).append('\n');
        return builder.toString();
    }

    private List<AiChatReferenceVO> buildUserReferences(TraceDetailVO detail, TraceSummaryVO summary) {
        List<AiChatReferenceVO> references = new ArrayList<>();
        Long batchId = detail.getBatchInfo() == null ? null : detail.getBatchInfo().getId();
        references.add(reference("trace", "当前商品批次 " + safeText(detail.getBatchInfo().getBatchCode()), batchId));
        references.add(reference("trace", "验真结果", batchId));
        if (detail.getInspectionReports() != null && !detail.getInspectionReports().isEmpty()) {
            references.add(reference("trace", "质检报告 " + safeText(detail.getInspectionReports().get(0).getReportNo()),
                    batchId));
        }
        if (summary.getRiskTips() != null && !summary.getRiskTips().isEmpty()) {
            references.add(reference("trace", "风险提示 " + summary.getRiskTips().get(0), batchId));
        }
        return references;
    }

    private AiChatReferenceVO reference(String type, String label, Long batchId) {
        return AiChatReferenceVO.builder()
                .type(type)
                .label(label)
                .batchId(batchId)
                .build();
    }

    private String buildStaffSystemPrompt() {
        return """
                你是农产品溯源管理后台的数据问答助手。
                你只能根据提供的企业内部上下文回答问题，不能编造不存在的数据。
                回答目标是帮助业务员理解当前单位的数据现状、风险重点和处理建议。
                回答要求：
                1. 先直接回答问题，再补一句依据。
                2. 如果问题涉及当前上下文没有的数据，必须明确说“当前上下文未提供该数据”。
                3. 不要泄露其他公司的数据，不要假设跨公司信息。
                4. 不要输出 markdown 表格。
                5. 如果上下文明确写了“当前问题已明确聚焦到该批次”，就必须优先围绕该批次回答，不要泛化成公司整体分析。
                6. 回答尽量简洁、可执行。
                """;
    }

    private String buildUserSystemPrompt() {
        return """
                你是农产品溯源小程序里的消费者问答助手。
                你只能根据当前商品的公开溯源信息回答问题，不能编造后台数据，也不能回答其他批次或企业内部经营数据。
                回答要求：
                1. 用通俗中文解释当前商品信息。
                2. 如果存在风险提示、召回、验真异常，要明确提醒用户谨慎处理。
                3. 不要做医学诊断，不要承诺绝对安全。
                4. 如果当前页面没有提供相关信息，要明确说“当前页面未提供该信息”。
                5. 回答尽量简洁、易懂。
                """;
    }

    private String buildStaffUserPrompt(String question, String context, Long batchId) {
        if (batchId != null) {
            return "问题：\n" + question + "\n\n注意：当前已指定聚焦批次，请优先回答该批次，不要泛化到整个公司。\n\n上下文：\n" + context + "\n\n请直接给出回答。";
        }
        return buildUserPrompt(question, context);
    }

    private String buildUserPrompt(String question, String context) {
        return "问题：\n" + question + "\n\n上下文：\n" + context + "\n\n请直接给出回答。";
    }

    private String askModel(String systemPrompt, String userPrompt) {
        try {
            String endpoint = normalizeBaseUrl() + "/chat/completions";
            String payloadJson = objectMapper.writeValueAsString(Map.of(
                    "model", model,
                    "temperature", temperature,
                    "messages", new Object[] {
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userPrompt)
                    }));

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofMillis(readTimeoutMs))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .POST(HttpRequest.BodyPublishers.ofString(payloadJson, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BusinessException(502, "AI 问答调用失败，状态码=" + response.statusCode());
            }

            JsonNode contentNode = objectMapper.readTree(response.body())
                    .path("choices").path(0).path("message").path("content");
            String answer = contentNode.asText("").trim();
            if (answer.isEmpty()) {
                throw new BusinessException(502, "AI 问答未返回有效内容");
            }
            return answer;
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error("AI chat invoke failed", exception);
            throw new BusinessException(502, "AI 问答服务暂时不可用");
        }
    }

    private String askModelOrFallback(String systemPrompt,
            String userPrompt,
            String question,
            TraceDetailVO detail,
            TraceSummaryVO summary) {
        try {
            return askModel(systemPrompt, userPrompt);
        } catch (BusinessException exception) {
            log.warn("AI user chat fallback triggered. reason={}", exception.getMessage());
            return buildUserFallbackAnswer(question, detail, summary);
        }
    }

    private String buildUserFallbackAnswer(String question, TraceDetailVO detail, TraceSummaryVO summary) {
        String productName = safeText(detail.getBatchInfo() == null ? null : detail.getBatchInfo().getProductName());
        String batchCode = safeText(detail.getBatchInfo() == null ? null : detail.getBatchInfo().getBatchCode());

        StringBuilder builder = new StringBuilder();
        builder.append("当前先根据该商品的公开追溯信息为你回答。");

        if (summary != null && summary.getSummaryText() != null && !summary.getSummaryText().isBlank()) {
            builder.append("\n").append(summary.getSummaryText().trim());
        } else {
            builder.append("\n")
                    .append(productName)
                    .append(" / 批次 ")
                    .append(batchCode)
                    .append(" 的公开信息已同步，可结合生产、质检和物流记录综合判断。");
        }

        if (summary != null && summary.getRiskTips() != null && !summary.getRiskTips().isEmpty()) {
            builder.append("\n风险提示：").append(summary.getRiskTips().get(0));
        }

        if (summary != null && summary.getActionTips() != null && !summary.getActionTips().isEmpty()) {
            builder.append("\n建议：").append(summary.getActionTips().get(0));
        } else if (detail.getRecallRecord() != null || detail.isRecallWarning()) {
            builder.append("\n建议：当前批次存在召回或风险提示，建议谨慎购买并联系销售方核实。");
        }

        String normalizedQuestion = question == null ? "" : question;
        if (normalizedQuestion.contains("物流") && sizeOf(detail.getLogisticsRecords()) > 0) {
            builder.append("\n当前可查询到 ").append(sizeOf(detail.getLogisticsRecords())).append(" 个物流节点。");
        }
        if ((normalizedQuestion.contains("质检") || normalizedQuestion.contains("安全"))
                && sizeOf(detail.getInspectionReports()) > 0) {
            builder.append("\n当前可查询到 ").append(sizeOf(detail.getInspectionReports())).append(" 份质检记录。");
        }

        builder.append("\n如果你愿意，也可以继续追问更具体的问题，比如“这个批次有没有异常质检”或“物流有没有中断”。");
        return builder.toString();
    }

    private String normalizeBaseUrl() {
        return (baseUrl == null || baseUrl.isBlank() ? "https://api.deepseek.com" : baseUrl).replaceAll("/+$", "");
    }

    private String joinOrDash(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "-";
        }
        return String.join("；", values);
    }

    private int sizeOf(List<?> values) {
        return values == null ? 0 : values.size();
    }

    private String safeText(Object value) {
        if (value == null) {
            return "-";
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? "-" : text;
    }

    private record StaffContextBundle(String context, List<AiChatReferenceVO> references) {
    }
}
