package com.example.vertexaiintegration.model;

/**
 * Enum representing the available Perplexity AI models.
 */
public enum PerplexityModel {
    // Sonar Range (Search models)
    SONAR("sonar", "Lightweight, cost-effective search model with grounding."),
    SONAR_PRO("sonar-pro", "Advanced search offering with grounding, supporting complex queries and follow-ups."),
    
    // Reasoning models
    SONAR_REASONING("sonar-reasoning", "Fast, real-time reasoning model designed for more problem-solving with search."),
    SONAR_REASONING_PRO("sonar-reasoning-pro", "Precise reasoning offering powered by DeepSeek-R1 with Chain of Thought (CoT).");
    
    private final String modelId;
    private final String description;
    
    PerplexityModel(String modelId, String description) {
        this.modelId = modelId;
        this.description = description;
    }
    
    public String getModelId() {
        return modelId;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Get a PerplexityModel by its model ID.
     * 
     * @param modelId The model ID to look up
     * @return The corresponding PerplexityModel, or SONAR if not found
     */
    public static PerplexityModel fromModelId(String modelId) {
        for (PerplexityModel model : values()) {
            if (model.getModelId().equals(modelId)) {
                return model;
            }
        }
        return SONAR; // Default to SONAR if not found
    }
}