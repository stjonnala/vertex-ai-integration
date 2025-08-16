package com.example.vertexaiintegration.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Objects;

@Schema(description = "Response model for a placed bracket order")
public class BracketOrderResponse {

    @Schema(description = "ID of the primary order", example = "b0b6dd9d-8b9b-48a9-ba46-b9d54906e415")
    private String orderId;

    @Schema(description = "ID of the take profit order", example = "8624e52d-69e7-4142-a7b1-1ee4d6d3d5f6")
    private String takeProfitOrderId;

    @Schema(description = "ID of the stop loss order", example = "23cb5cad-5c4b-4259-a6e7-66a225f1c6d8")
    private String stopLossOrderId;

    @Schema(description = "Ticker symbol of the stock traded", example = "AAPL")
    private String ticker;

    @Schema(description = "Dollar amount invested", example = "3000.00")
    private BigDecimal amount;

    @Schema(description = "Quantity of shares purchased", example = "20")
    private int quantity;

    @Schema(description = "Status of the order", example = "accepted")
    private String status;

    @Schema(description = "Error message if order placement failed", example = "Insufficient funds")
    private String errorMessage;

    // No-args constructor
    public BracketOrderResponse() {
    }

    // All-args constructor
    public BracketOrderResponse(String orderId, String takeProfitOrderId, String stopLossOrderId, 
                               String ticker, BigDecimal amount, int quantity, 
                               String status, String errorMessage) {
        this.orderId = orderId;
        this.takeProfitOrderId = takeProfitOrderId;
        this.stopLossOrderId = stopLossOrderId;
        this.ticker = ticker;
        this.amount = amount;
        this.quantity = quantity;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String orderId;
        private String takeProfitOrderId;
        private String stopLossOrderId;
        private String ticker;
        private BigDecimal amount;
        private int quantity;
        private String status;
        private String errorMessage;

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder takeProfitOrderId(String takeProfitOrderId) {
            this.takeProfitOrderId = takeProfitOrderId;
            return this;
        }

        public Builder stopLossOrderId(String stopLossOrderId) {
            this.stopLossOrderId = stopLossOrderId;
            return this;
        }

        public Builder ticker(String ticker) {
            this.ticker = ticker;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public BracketOrderResponse build() {
            return new BracketOrderResponse(orderId, takeProfitOrderId, stopLossOrderId, 
                                           ticker, amount, quantity, status, errorMessage);
        }
    }

    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTakeProfitOrderId() {
        return takeProfitOrderId;
    }

    public void setTakeProfitOrderId(String takeProfitOrderId) {
        this.takeProfitOrderId = takeProfitOrderId;
    }

    public String getStopLossOrderId() {
        return stopLossOrderId;
    }

    public void setStopLossOrderId(String stopLossOrderId) {
        this.stopLossOrderId = stopLossOrderId;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BracketOrderResponse that = (BracketOrderResponse) o;
        return quantity == that.quantity &&
                Objects.equals(orderId, that.orderId) &&
                Objects.equals(takeProfitOrderId, that.takeProfitOrderId) &&
                Objects.equals(stopLossOrderId, that.stopLossOrderId) &&
                Objects.equals(ticker, that.ticker) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(status, that.status) &&
                Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, takeProfitOrderId, stopLossOrderId, ticker, amount, quantity, status, errorMessage);
    }

    // toString
    @Override
    public String toString() {
        return "BracketOrderResponse{" +
                "orderId='" + orderId + '\'' +
                ", takeProfitOrderId='" + takeProfitOrderId + '\'' +
                ", stopLossOrderId='" + stopLossOrderId + '\'' +
                ", ticker='" + ticker + '\'' +
                ", amount=" + amount +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
