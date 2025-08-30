package com.example.vertexaiintegration.config;

import com.example.vertexaiintegration.model.PerplexityModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PerplexityConfig {

    @Value("${perplexity.api.key}")
    private String apiKey;

    @Value("${perplexity.api.base-url}")
    private String baseUrl;

    @Value("${perplexity.api.model:sonar}")
    private String modelId;

    @Value("${perplexity.api.max-tokens:1000}")
    private int maxTokens;

    // Getters
    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getModelId() {
        return modelId;
    }

    public PerplexityModel getModel() {
        return PerplexityModel.fromModelId(modelId);
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
