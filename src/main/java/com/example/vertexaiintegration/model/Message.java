package com.example.vertexaiintegration.model;

/**
 * Represents a message in a conversation with Perplexity AI.
 */
public class Message {
    private String role;
    private String content;

    // Default constructor
    public Message() {
    }

    // Constructor with parameters
    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    // Getters and setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}