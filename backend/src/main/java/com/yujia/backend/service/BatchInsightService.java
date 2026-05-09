package com.yujia.backend.service;

import com.yujia.backend.vo.BatchArchiveVO;
import com.yujia.backend.vo.BatchInsightVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchInsightService {

    private final BatchArchiveService batchArchiveService;

    public BatchInsightVO insight(Long batchId) {
        return insight(batchArchiveService.archive(batchId));
    }

    public BatchInsightVO insight(BatchArchiveVO archive) {
        List<String> missingItems = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> nextActions = new ArrayList<>();

        int completeness = 100;
        if (!StringUtils.hasText(archive.getBatchInfo().getProductCategory())) {
            completeness -= 8;
            missingItems.add("缺少产品品类");
        }
        if (archive.getBatchInfo().getPlantingDate() == null) {
            completeness -= 8;
            missingItems.add("缺少种植日期");
        }
        if (archive.getProductionRecords().isEmpty()) {
            completeness -= 20;
            missingItems.add("未录入生产记录");
            nextActions.add("尽快补录至少一条生产记录");
        }
        if (archive.getInspectionReports().isEmpty()) {
            completeness -= 20;
            missingItems.add("未上传质检报告");
            nextActions.add("安排质检并上传报告");
        }
        if (archive.getLogisticsRecords().isEmpty()) {
            completeness -= 15;
            missingItems.add("缺少物流节点");
            nextActions.add("补充入库、运输或签收节点");
        }
        if (archive.getProductItems().isEmpty()) {
            completeness -= 12;
            missingItems.add("未生成单品码");
            nextActions.add("生成一物一码数据");
        }
        if (!StringUtils.hasText(archive.getBaseInfo().getManagerName())) {
            completeness -= 5;
            missingItems.add("基地负责人缺失");
        }
        if (!StringUtils.hasText(archive.getBaseInfo().getContactPhone())) {
            completeness -= 5;
            missingItems.add("基地联系电话缺失");
        }
        completeness = Math.max(0, completeness);

        int risk = 5;
        if (archive.getBatchInfo().getRecallStatus() != null && archive.getBatchInfo().getRecallStatus() == 1) {
            risk += 45;
            warnings.add("批次当前处于召回中");
        }
        boolean failedInspection = archive.getInspectionReports().stream()
                .anyMatch(report -> report.getResultStatus() != null && report.getResultStatus() != 1);
        if (failedInspection) {
            risk += 25;
            warnings.add("存在非合格质检报告");
            nextActions.add("复核质检结论并评估召回");
        }
        if (archive.getHighPriorityFeedbackCount() > 0) {
            risk += 15 + Math.min(archive.getHighPriorityFeedbackCount() * 3, 12);
            warnings.add("存在高优先级用户反馈");
            nextActions.add("优先处理质量或物流投诉");
        }
        if (archive.getAbnormalScanCount() >= 3) {
            risk += 15;
            warnings.add("异常扫码次数偏高");
            nextActions.add("核验包装、防伪签名和流向");
        } else if (archive.getScanCount() >= 8) {
            risk += 8;
            warnings.add("扫码次数较多，建议关注流通环节");
        }
        if (completeness < 70) {
            risk += 12;
            warnings.add("档案完整度偏低");
        }
        risk = Math.min(100, risk);

        if (warnings.isEmpty()) {
            warnings.add("未发现明显风险，建议按计划更新档案");
        }
        if (nextActions.isEmpty()) {
            nextActions.add("保持当前节奏，持续补充流通与质检数据");
        }

        BatchInsightVO vo = new BatchInsightVO();
        vo.setBatchId(archive.getBatchInfo().getId());
        vo.setCompletenessScore(completeness);
        vo.setRiskScore(risk);
        vo.setRiskLevel(toRiskLevel(risk));
        vo.setMissingItems(missingItems);
        vo.setWarnings(warnings);
        vo.setNextActions(nextActions);
        vo.setAiSummary(buildSummary(archive, completeness, risk, warnings, nextActions));
        return vo;
    }

    private String toRiskLevel(int risk) {
        if (risk >= 70) {
            return "HIGH";
        }
        if (risk >= 40) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private String buildSummary(BatchArchiveVO archive, int completeness, int risk, List<String> warnings, List<String> nextActions) {
        StringBuilder builder = new StringBuilder();
        builder.append("批次 ").append(archive.getBatchInfo().getBatchCode())
                .append(" 当前档案完整度 ").append(completeness).append("%，风险评分 ").append(risk).append("。")
                .append("重点提示：").append(warnings.get(0)).append("。");
        if (!nextActions.isEmpty()) {
            builder.append("建议优先执行：").append(nextActions.get(0)).append("。");
        }
        return builder.toString();
    }
}
