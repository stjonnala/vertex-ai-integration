package com.example.vertexaiintegration.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Objects;

@Schema(description = "Request model for placing a bracket order")
public class BracketOrderRequest {

    @Schema(description = "Ticker symbol of the stock to trade", example = "AAPL", required = true)
    private String ticker;

    @Schema(description = "Dollar amount to invest (e.g., $3000, $4000)", example = "3000.00", required = true)
    private BigDecimal amount;

    @Schema(description = "Limit price for the entry order", example = "150.00", required = true)
    private BigDecimal limitPrice;

    @Schema(description = "Take profit price", example = "155.00", required = true)
    private BigDecimal takeProfitPrice;

    @Schema(description = "Stop loss price", example = "145.00", required = true)
    private BigDecimal stopLossPrice;

    // No-args constructor
    public BracketOrderRequest() {
    }

    // All-args constructor
    public BracketOrderRequest(String ticker, BigDecimal amount, BigDecimal limitPrice, BigDecimal takeProfitPrice, BigDecimal stopLossPrice) {
        this.ticker = ticker;
        this.amount = amount;
        this.limitPrice = limitPrice;
        this.takeProfitPrice = takeProfitPrice;
        this.stopLossPrice = stopLossPrice;
    }

    // Getters and setters
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

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
    }

    public BigDecimal getTakeProfitPrice() {
        return takeProfitPrice;
    }

    public void setTakeProfitPrice(BigDecimal takeProfitPrice) {
        this.takeProfitPrice = takeProfitPrice;
    }

    public BigDecimal getStopLossPrice() {
        return stopLossPrice;
    }

    public void setStopLossPrice(BigDecimal stopLossPrice) {
        this.stopLossPrice = stopLossPrice;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BracketOrderRequest that = (BracketOrderRequest) o;
        return Objects.equals(ticker, that.ticker) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(limitPrice, that.limitPrice) &&
                Objects.equals(takeProfitPrice, that.takeProfitPrice) &&
                Objects.equals(stopLossPrice, that.stopLossPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, amount, limitPrice, takeProfitPrice, stopLossPrice);
    }

    // toString
    @Override
    public String toString() {
        return "BracketOrderRequest{" +
                "ticker='" + ticker + '\'' +
                ", amount=" + amount +
                ", limitPrice=" + limitPrice +
                ", takeProfitPrice=" + takeProfitPrice +
                ", stopLossPrice=" + stopLossPrice +
                '}';
    }
}
