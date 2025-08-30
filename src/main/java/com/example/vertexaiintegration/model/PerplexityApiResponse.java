package com.example.vertexaiintegration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Model class for the actual API response from Perplexity.
 * This is used internally by the service class.
 */
public class PerplexityApiResponse {

    private String id;
    private String model;

    @JsonProperty("created")
    private long createdTimestamp;

    private List<Choice> choices;

    // Default constructor
    public PerplexityApiResponse() {
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    /**
     * For backward compatibility with existing code
     * @return The text of the first choice's message content, or null if not available
     */
    public String getText() {
        if (choices != null && !choices.isEmpty() && choices.get(0).getMessage() != null) {
            return choices.get(0).getMessage().getContent();
        }
        return null;
    }

    /**
     * Represents a choice in the response
     */
    public static class Choice {
        private int index;
        private Message message;

        public Choice() {
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }
}
