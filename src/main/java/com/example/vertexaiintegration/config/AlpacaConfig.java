package com.example.vertexaiintegration.config;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.properties.DataAPIType;
import net.jacobpeterson.alpaca.model.properties.EndpointAPIType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlpacaConfig {

    @Value("${alpaca.api.key}")
    private String apiKey;

    @Value("${alpaca.api.secret}")
    private String apiSecret;

    @Value("${alpaca.api.base-url}")
    private String baseUrl;

    // Getters
    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    @Bean
    public AlpacaAPI alpacaAPI() {
        // Using paper trading environment
        return new AlpacaAPI(apiKey, apiSecret, EndpointAPIType.PAPER, DataAPIType.IEX);
    }
}
