package com.example.vertexaiintegration.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request model for placing an order")
public class OrderRequest {

    @Schema(description = "Ticker symbol of the stock to trade", example = "AAPL", required = true)
    private String ticker;

    @Schema(description = "Number of stocks to trade", example = "10", required = true)
    private int quantity;

    // Default constructor
    public OrderRequest() {
    }

    // Constructor with parameters
    public OrderRequest(String ticker, int quantity) {
        this.ticker = ticker;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "ticker='" + ticker + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}