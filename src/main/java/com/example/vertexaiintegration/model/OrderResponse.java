package com.example.vertexaiintegration.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response model for a placed order")
public class OrderResponse {

    @Schema(description = "ID of the order", example = "b0b6dd9d-8b9b-48a9-ba46-b9d54906e415")
    private String orderId;

    @Schema(description = "Ticker symbol of the stock traded", example = "AAPL")
    private String ticker;

    @Schema(description = "Number of stocks traded", example = "10")
    private int quantity;

    @Schema(description = "Side of the order (buy or sell)", example = "buy")
    private String side;

    @Schema(description = "Status of the order", example = "accepted")
    private String status;

    @Schema(description = "Error message if order placement failed", example = "Insufficient funds")
    private String errorMessage;

    // Default constructor
    public OrderResponse() {
    }

    // Constructor with parameters
    public OrderResponse(String orderId, String ticker, int quantity, String side, String status) {
        this.orderId = orderId;
        this.ticker = ticker;
        this.quantity = quantity;
        this.side = side;
        this.status = status;
    }

    // Constructor for error response
    public OrderResponse(String ticker, int quantity, String side, String status, String errorMessage) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.side = side;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

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

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
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
        return "OrderResponse{" +
                "orderId='" + orderId + '\'' +
                ", ticker='" + ticker + '\'' +
                ", quantity=" + quantity +
                ", side='" + side + '\'' +
                ", status='" + status + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}