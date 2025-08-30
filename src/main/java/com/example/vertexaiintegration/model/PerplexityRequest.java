package com.example.vertexaiintegration.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request model for Perplexity AI query")
public class PerplexityRequest {

    @Schema(description = "The question to ask Perplexity AI", example = "What is the capital of France?", required = true)
    private String question;

    @Schema(description = "The model to use for the query (optional)", example = "sonar", 
            allowableValues = {"sonar", "sonar-pro", "sonar-reasoning", "sonar-reasoning-pro"})
    private String model;

    // Default constructor
    public PerplexityRequest() {
    }

    // Constructor with parameters
    public PerplexityRequest(String question) {
        this.question = question;
    }

    // Constructor with model
    public PerplexityRequest(String question, String model) {
        this.question = question;
        this.model = model;
    }

    // Getters and setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "PerplexityRequest{" +
                "question='" + question + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
