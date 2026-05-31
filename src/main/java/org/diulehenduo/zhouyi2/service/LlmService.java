package org.diulehenduo.zhouyi2.service;

import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 大模型调用服务（兼容 OpenAI Chat API 格式）
 * <p>
 * 支持 OpenAI、Azure OpenAI、通义千问、DeepSeek、Ollama 等兼容接口。
 * </p>
 */
@Service
public class LlmService {

    private static final Logger log = LoggerFactory.getLogger(LlmService.class);

    private final RestClient restClient;
    private final LlmProperties properties;

    public LlmService(RestClient.Builder restClientBuilder, LlmProperties properties) {
        this.properties = properties;
        this.restClient = restClientBuilder
                .baseUrl(properties.getApiUrl())
                .defaultHeader("Authorization", "Bearer " + properties.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * 调用大模型生成解读
     *
     * @param prompt 提示词（包含卦象信息和用户问题）
     * @return 模型回复文本
     */
    public String chat(String prompt) {
        try {
            Map<String, Object> requestBody = buildRequestBody(prompt);
            log.debug("LLM request: model={}, prompt length={}", properties.getModel(), prompt.length());

            Map<String, Object> response = restClient.post()
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                log.warn("LLM returned null response");
                return null;
            }

            return extractContent(response);

        } catch (Exception e) {
            log.error("LLM API call failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 构建 OpenAI 兼容的请求体
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> buildRequestBody(String prompt) {
        return Map.of(
                "model", properties.getModel(),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt()),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", properties.getTemperature(),
                "max_tokens", properties.getMaxTokens()
        );
    }

    /**
     * System prompt - 定义大模型的角色和行为
     */
    private String systemPrompt() {
        return """
                你是精通《周易》的算命大师，擅长用六十四卦为人们答疑解惑。
                你的特点：
                1. 精通卦象、卦辞、爻辞，理解各卦的深层含义
                2. 善于将卦理与现实问题结合，给出切实可行的建议
                3. 语言风格古典而不晦涩，让提问者听得懂
                4. 保持谦逊，知道天机不可尽泄，给提示而非定数
                5. 解读时长结合变爻、体用生克关系进行分析
                6. 最终给出明确的建议方向，让提问者有所依循
                """;
    }

    /**
     * 从响应中提取文本内容
     */
    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                if (message != null) {
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract LLM response content: {}", e.getMessage());
        }
        return null;
    }

    /**
     * LLM 配置属性
     */
    public static class LlmProperties {
        private String apiUrl;
        private String apiKey;
        private String model;
        private double temperature;
        private int maxTokens;

        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    }
}
