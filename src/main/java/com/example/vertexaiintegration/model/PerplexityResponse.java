package com.example.vertexaiintegration.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response model for Perplexity AI query")
public class PerplexityResponse {

    @Schema(description = "The answer from Perplexity AI", example = "The capital of France is Paris.")
    private String answer;

    @Schema(description = "Status of the query", example = "success")
    private String status;

    @Schema(description = "Error message if query failed", example = "API key is invalid")
    private String errorMessage;

    // Default constructor
    public PerplexityResponse() {
    }

    // Constructor with parameters for successful response
    public PerplexityResponse(String answer) {
        this.answer = answer;
        this.status = "success";
    }

    // Constructor for error response
    public PerplexityResponse(String status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    // Getters and setters
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "PerplexityResponse{" +
                "answer='" + answer + '\'' +
                ", status='" + status + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}