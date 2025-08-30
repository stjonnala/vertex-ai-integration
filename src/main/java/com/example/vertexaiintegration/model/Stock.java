package com.example.vertexaiintegration.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a stock recommendation based on Warren Buffett's investment strategy.
 */
@Schema(description = "Stock recommendation model")
public class Stock {

    @Schema(description = "Stock ticker symbol", example = "AAPL")
    private String ticker;

    @Schema(description = "Company name", example = "Apple Inc.")
    private String companyName;

    @Schema(description = "Current stock price", example = "150.25")
    private double currentPrice;

    @Schema(description = "Brief reason for recommendation", example = "Strong fundamentals, consistent growth, and competitive advantage")
    private String reasonForRecommendation;

    @Schema(description = "Potential upside percentage", example = "15.5")
    private double potentialUpside;

    // Default constructor
    public Stock() {
    }

    // Constructor with parameters
    public Stock(String ticker, String companyName, double currentPrice, String reasonForRecommendation, double potentialUpside) {
        this.ticker = ticker;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
        this.reasonForRecommendation = reasonForRecommendation;
        this.potentialUpside = potentialUpside;
    }

    // Getters and setters
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getReasonForRecommendation() {
        return reasonForRecommendation;
    }

    public void setReasonForRecommendation(String reasonForRecommendation) {
        this.reasonForRecommendation = reasonForRecommendation;
    }

    public double getPotentialUpside() {
        return potentialUpside;
    }

    public void setPotentialUpside(double potentialUpside) {
        this.potentialUpside = potentialUpside;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "ticker='" + ticker + '\'' +
                ", companyName='" + companyName + '\'' +
                ", currentPrice=" + currentPrice +
                ", reasonForRecommendation='" + reasonForRecommendation + '\'' +
                ", potentialUpside=" + potentialUpside +
                '}';
    }
}