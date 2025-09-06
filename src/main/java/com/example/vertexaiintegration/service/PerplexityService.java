package com.example.vertexaiintegration.service;

import com.example.vertexaiintegration.config.PerplexityConfig;
import com.example.vertexaiintegration.model.Message;
import com.example.vertexaiintegration.model.PerplexityApiRequest;
import com.example.vertexaiintegration.model.PerplexityApiResponse;
import com.example.vertexaiintegration.model.PerplexityModel;
import com.example.vertexaiintegration.model.PerplexityRequest;
import com.example.vertexaiintegration.model.PerplexityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class PerplexityService {

    private static final Logger log = LoggerFactory.getLogger(PerplexityService.class);

    private final RestTemplate restTemplate;
    private final PerplexityConfig perplexityConfig;

    public PerplexityService(RestTemplate restTemplate, PerplexityConfig perplexityConfig) {
        this.restTemplate = restTemplate;
        this.perplexityConfig = perplexityConfig;
    }

    /**
     * Sends a query to Perplexity AI and returns the response.
     *
     * @param request The request containing the question to ask
     * @return The response from Perplexity AI
     */
    public PerplexityResponse query(PerplexityRequest request) {
        log.info("Sending query to Perplexity AI: {}", request.getQuestion());

        try {
            // Create headers with API key
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + perplexityConfig.getApiKey());

            // Create a list of messages with the user's question
            List<Message> messages = new ArrayList<>();
            messages.add(new Message("user", request.getQuestion()));

            // Determine which model to use (from request or config)
            String modelId;
            if (request.getModel() != null && !request.getModel().trim().isEmpty()) {
                modelId = request.getModel();
                log.info("Using model specified in request: {}", modelId);
            } else {
                modelId = perplexityConfig.getModelId();
                log.info("Using default model from configuration: {}", modelId);
            }

            // Create the API request
            PerplexityApiRequest apiRequest = new PerplexityApiRequest(
                    modelId,
                    messages,
                    perplexityConfig.getMaxTokens()
            );

            // Create the HTTP entity with headers and body
            HttpEntity<PerplexityApiRequest> entity = new HttpEntity<>(apiRequest, headers);

            // Make the API call
            String url = perplexityConfig.getBaseUrl() + "/chat/completions";
            log.debug("Sending request to URL: {}", url);
            log.debug("Using model: {}", modelId);

            PerplexityApiResponse apiResponse = restTemplate.postForObject(
                    url,
                    entity,
                    PerplexityApiResponse.class
            );

            // Process the response
            if (apiResponse != null && apiResponse.getText() != null) {
                log.info("Received response from Perplexity AI");
                log.info("Full response from Perplexity AI: {}", apiResponse.getText());
                return new PerplexityResponse(apiResponse.getText());
            } else {
                log.error("Received null or empty response from Perplexity AI");
                return new PerplexityResponse("FAILED", "Received null or empty response from Perplexity AI");
            }
        } catch (RestClientException e) {
            log.error("Error querying Perplexity AI: {}", e.getMessage(), e);
            return new PerplexityResponse("FAILED", "Error querying Perplexity AI: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return new PerplexityResponse("FAILED", "Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Simplified method that sends a query to Perplexity AI and returns the response.
     * This method always uses the default model from the configuration.
     *
     * @param question The question to ask
     * @return The response from Perplexity AI
     */
    public PerplexityResponse querySimple(String question) {
        log.info("Sending simple query to Perplexity AI: {}", question);

        // Create a PerplexityRequest with just the question
        PerplexityRequest request = new PerplexityRequest(question);

        // Use the existing query method
        return query(request);
    }
}
