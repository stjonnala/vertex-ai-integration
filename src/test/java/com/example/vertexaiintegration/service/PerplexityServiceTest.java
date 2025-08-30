package com.example.vertexaiintegration.service;

import com.example.vertexaiintegration.config.PerplexityConfig;
import com.example.vertexaiintegration.model.PerplexityRequest;
import com.example.vertexaiintegration.model.PerplexityResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class PerplexityServiceTest {

    @Autowired
    private PerplexityService perplexityService;

    @Autowired
    private PerplexityConfig perplexityConfig;

    @Test
    public void testPerplexityQuery() {
        // Print API key and base URL for debugging
        System.out.println("[DEBUG_LOG] API Key: " + perplexityConfig.getApiKey());
        System.out.println("[DEBUG_LOG] Base URL: " + perplexityConfig.getBaseUrl());

        // Create a request
        PerplexityRequest request = new PerplexityRequest("What is the capital of France?");

        // Send the request to Perplexity AI
        PerplexityResponse response = perplexityService.query(request);

        // Print the response for debugging
        System.out.println("[DEBUG_LOG] Response status: " + response.getStatus());
        System.out.println("[DEBUG_LOG] Response answer: " + response.getAnswer());
        System.out.println("[DEBUG_LOG] Response error message: " + response.getErrorMessage());

        // Assert that the response is not null
        assertNotNull(response);

        // Assert that the status is "success"
        assertEquals("success", response.getStatus());

        // Assert that the answer is not null or empty
        assertNotNull(response.getAnswer());
        assertNotEquals("", response.getAnswer().trim());
    }

    @Test
    public void testPerplexityQuerySimple() {
        // Print API key and base URL for debugging
        System.out.println("[DEBUG_LOG] API Key: " + perplexityConfig.getApiKey());
        System.out.println("[DEBUG_LOG] Base URL: " + perplexityConfig.getBaseUrl());
        System.out.println("[DEBUG_LOG] Default Model: " + perplexityConfig.getModelId());

        // Send a simple question to Perplexity AI
        PerplexityResponse response = perplexityService.querySimple("What is the capital of France?");

        // Print the response for debugging
        System.out.println("[DEBUG_LOG] Response status: " + response.getStatus());
        System.out.println("[DEBUG_LOG] Response answer: " + response.getAnswer());
        System.out.println("[DEBUG_LOG] Response error message: " + response.getErrorMessage());

        // Assert that the response is not null
        assertNotNull(response);

        // Assert that the status is "success"
        assertEquals("success", response.getStatus());

        // Assert that the answer is not null or empty
        assertNotNull(response.getAnswer());
        assertNotEquals("", response.getAnswer().trim());
    }
}
