package com.example.vertexaiintegration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Model class for the actual API request to Perplexity.
 * This is used internally by the service class.
 */
public class PerplexityApiRequest {

    private String model;
    private List<Message> messages;

    @JsonProperty("max_tokens")
    private int maxTokens;

    // Default constructor
    public PerplexityApiRequest() {
    }

    // Constructor with parameters
    public PerplexityApiRequest(String model, List<Message> messages, int maxTokens) {
        this.model = model;
        this.messages = messages;
        this.maxTokens = maxTokens;
    }

    // Getters and setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
}
