package org.diulehenduo.zhouyi2.config;

import org.diulehenduo.zhouyi2.service.LlmService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * 应用配置
 * <p>
 * 声明 RestClient Bean 和 LLM 配置属性。
 * </p>
 */
@Configuration
public class AppConfig {

    /**
     * LLM 配置属性
     */
    @Bean
    @ConfigurationProperties(prefix = "llm")
    public LlmService.LlmProperties llmProperties() {
        return new LlmService.LlmProperties();
    }

    /**
     * REST 客户端构建器（用于调用 LLM API）
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
