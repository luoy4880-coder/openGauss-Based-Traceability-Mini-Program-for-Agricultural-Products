package com.yujia.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackAiClassifierService {

    private static final String FALLBACK_SUMMARY = "建议先确认反馈证据并联系处理人跟进闭环。";

    private final ObjectMapper objectMapper;

    @Value("${app.ai.feedback.enabled:true}")
    private boolean enabled;

    @Value("${app.ai.feedback.api-key:}")
    private String apiKey;

    @Value("${app.ai.feedback.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${app.ai.feedback.model:deepseek-chat}")
    private String model;

    @Value("${app.ai.feedback.temperature:0.1}")
    private double temperature;

    @Value("${app.ai.feedback.connect-timeout-ms:3000}")
    private int connectTimeoutMs;

    @Value("${app.ai.feedback.read-timeout-ms:6000}")
    private int readTimeoutMs;

    @Value("${app.ai.feedback.system-prompt}")
    private String systemPrompt;

    public AiClassifyResult classify(String type, String content) {
        AiClassifyResult result;
        if (enabled && apiKey != null && !apiKey.isBlank()) {
            try {
                result = classifyWithDeepSeek(type, content);
            } catch (Exception ex) {
                log.warn("DeepSeek classify failed, fallback to rule-based: {}", ex.getMessage());
                result = classifyByRule(type, content);
            }
        } else {
            result = classifyByRule(type, content);
        }
        return rectifyByRiskKeywords(type, content, result);
    }

    private AiClassifyResult classifyWithDeepSeek(String type, String content) throws Exception {
        String endpoint = (baseUrl == null || baseUrl.isBlank() ? "https://api.deepseek.com" : baseUrl).replaceAll("/+$", "")
                + "/chat/completions";

        Map<String, Object> payload = Map.of(
                "model", model,
                "temperature", temperature,
                "messages", new Object[]{
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", buildUserPrompt(type, content))
                }
        );
        String payloadJson = objectMapper.writeValueAsString(payload);

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

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("DeepSeek status=" + response.statusCode());
        }

        JsonNode contentNode = objectMapper.readTree(response.body())
                .path("choices").path(0).path("message").path("content");
        if (contentNode.isMissingNode() || contentNode.asText().isBlank()) {
            throw new IllegalStateException("DeepSeek content missing");
        }
        return parseModelResult(contentNode.asText());
    }

    private String buildUserPrompt(String type, String content) {
        return "反馈类型: " + safeText(type) + "\n"
                + "反馈内容: " + safeText(content) + "\n"
                + "请严格输出 JSON，且只能输出 JSON。";
    }

    private AiClassifyResult parseModelResult(String rawText) throws Exception {
        JsonNode node = objectMapper.readTree(extractJson(rawText));
        String category = normalizeCategory(node.path("category").asText(""));
        Integer priority = normalizePriority(node.path("priority").asInt(3));
        String riskLevel = normalizeRiskLevel(node.path("riskLevel").asText(""));
        String summary = node.path("summary").asText("").trim();
        if (summary.isEmpty()) {
            summary = FALLBACK_SUMMARY;
        }
        if (riskLevel == null || riskLevel.isBlank()) {
            riskLevel = inferRiskLevel(category, priority);
        }
        return new AiClassifyResult(category, priority, riskLevel, summary);
    }

    private AiClassifyResult rectifyByRiskKeywords(String type, String content, AiClassifyResult input) {
        String text = (safeText(type) + " " + safeText(content)).toLowerCase(Locale.ROOT);
        String category = input.category();
        int priority = input.priority() == null ? 3 : input.priority();
        String summary = input.summary() == null || input.summary().isBlank() ? FALLBACK_SUMMARY : input.summary();

        if (containsAny(text, "变质", "异味", "霉", "不合格", "农残", "中毒", "过敏", "腐坏", "发霉")) {
            category = "质量";
            priority = 1;
            if (!summary.contains("批次") && !summary.contains("质检")) {
                summary = "建议立即核查批次与质检记录，并通知业务人员优先处理。";
            }
            return new AiClassifyResult(category, priority, "HIGH", summary);
        }

        if (containsAny(text, "物流", "快递", "配送", "延迟", "破损", "丢件", "冷链")) {
            if ("其他".equals(category)) {
                category = "物流";
            }
            priority = Math.min(priority, 2);
            return new AiClassifyResult(category, priority, inferRiskLevel(category, priority), summary);
        }

        if (containsAny(text, "客服", "服务", "态度", "售后", "回复慢", "不处理")) {
            if ("其他".equals(category)) {
                category = "服务";
            }
            return new AiClassifyResult(category, priority, inferRiskLevel(category, priority), summary);
        }
        return new AiClassifyResult(category, priority, inferRiskLevel(category, priority), summary);
    }

    private String extractJson(String text) {
        String cleaned = text == null ? "" : text.trim();
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return cleaned.substring(start, end + 1);
        }
        return cleaned;
    }

    private String normalizeCategory(String raw) {
        String s = raw == null ? "" : raw.toLowerCase(Locale.ROOT);
        if (s.contains("质量") || s.contains("quality")) {
            return "质量";
        }
        if (s.contains("物流") || s.contains("logistics")) {
            return "物流";
        }
        if (s.contains("服务") || s.contains("service")) {
            return "服务";
        }
        return "其他";
    }

    private Integer normalizePriority(int raw) {
        if (raw < 1 || raw > 3) {
            return 3;
        }
        return raw;
    }

    private String normalizeRiskLevel(String raw) {
        String value = raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
        if ("HIGH".equals(value) || "MEDIUM".equals(value) || "LOW".equals(value)) {
            return value;
        }
        return "";
    }

    private String inferRiskLevel(String category, Integer priority) {
        if (priority != null && priority <= 1) {
            return "HIGH";
        }
        if ("质量".equals(category) || priority != null && priority == 2) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private AiClassifyResult classifyByRule(String type, String content) {
        String text = (safeText(type) + " " + safeText(content)).toLowerCase(Locale.ROOT);
        if (containsAny(text, "质量", "变质", "异味", "发霉", "坏", "不合格", "农残", "中毒", "过敏")) {
            return new AiClassifyResult("质量", 1, "HIGH", "建议优先核查批次质量与检验记录，并联系用户回访。");
        }
        if (containsAny(text, "物流", "快递", "配送", "延迟", "破损", "丢件", "冷链")) {
            return new AiClassifyResult("物流", 2, "MEDIUM", "建议核对物流轨迹与签收节点，确认补发或赔付方案。");
        }
        if (containsAny(text, "服务", "客服", "态度", "售后", "回复慢")) {
            return new AiClassifyResult("服务", 3, "LOW", "建议安排客服回访并记录处理过程，明确完成时限。");
        }
        return new AiClassifyResult("其他", 3, "LOW", FALLBACK_SUMMARY);
    }

    private boolean containsAny(String source, String... keywords) {
        for (String keyword : keywords) {
            if (source.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String safeText(String text) {
        return text == null ? "" : text.trim();
    }

    public static class AiClassifyResult {
        private final String category;
        private final Integer priority;
        private final String riskLevel;
        private final String summary;

        public AiClassifyResult(String category, Integer priority, String riskLevel, String summary) {
            this.category = category;
            this.priority = priority;
            this.riskLevel = riskLevel;
            this.summary = summary;
        }

        public String category() {
            return category;
        }

        public Integer priority() {
            return priority;
        }

        public String riskLevel() {
            return riskLevel;
        }

        public String summary() {
            return summary;
        }
    }
}
