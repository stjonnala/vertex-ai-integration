package com.example.vertexaiintegration.service;

import com.example.vertexaiintegration.model.BracketOrderRequest;
import com.example.vertexaiintegration.model.BracketOrderResponse;
import net.jacobpeterson.alpaca.AlpacaAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AlpacaService {

    private static final Logger log = LoggerFactory.getLogger(AlpacaService.class);

    private final AlpacaAPI alpacaAPI;

    // Constructor
    public AlpacaService(AlpacaAPI alpacaAPI) {
        this.alpacaAPI = alpacaAPI;
    }

    // Mock current prices for demo purposes
    private static final Map<String, BigDecimal> MOCK_PRICES = new HashMap<>();

    static {
        MOCK_PRICES.put("AAPL", new BigDecimal("150.00"));
        MOCK_PRICES.put("MSFT", new BigDecimal("300.00"));
        MOCK_PRICES.put("GOOGL", new BigDecimal("2500.00"));
        MOCK_PRICES.put("AMZN", new BigDecimal("3000.00"));
        MOCK_PRICES.put("TSLA", new BigDecimal("800.00"));
        MOCK_PRICES.put("FB", new BigDecimal("350.00"));
        MOCK_PRICES.put("NFLX", new BigDecimal("500.00"));
    }

    /**
     * Gets the current price of a stock (mock implementation).
     * 
     * @param ticker The ticker symbol of the stock
     * @return The current price of the stock
     */
    public BigDecimal getCurrentPrice(String ticker) {
        log.info("Getting current price for ticker: {}", ticker);

        // In a real implementation, you would use the Alpaca API to get the current price
        // For demo purposes, we'll use mock prices
        BigDecimal price = MOCK_PRICES.getOrDefault(ticker.toUpperCase(), new BigDecimal("100.00"));
        log.info("Current price for ticker {}: ${}", ticker, price);

        return price;
    }

    /**
     * Calculates the quantity of shares to buy based on the dollar amount and current price.
     * 
     * @param amount The dollar amount to invest
     * @param currentPrice The current price of the stock
     * @return The quantity of shares to buy
     */
    public int calculateQuantity(BigDecimal amount, BigDecimal currentPrice) {
        if (currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Current price must be greater than zero");
        }

        // Calculate quantity: amount / currentPrice, rounded down to nearest whole number
        int quantity = amount.divide(currentPrice, 0, RoundingMode.DOWN).intValue();
        log.info("Calculated quantity: {} shares (${} at ${} per share)", quantity, amount, currentPrice);

        return quantity;
    }

    /**
     * Places a bracket order in Alpaca.
     * 
     * Note: This is a simplified implementation. In a real-world scenario,
     * you would need to use the appropriate Alpaca API methods to place the order.
     * 
     * @param request The bracket order request
     * @return The bracket order response
     */
    public BracketOrderResponse placeBracketOrder(BracketOrderRequest request) {
        try {
            log.info("Processing bracket order for ticker: {}, dollar amount: {}, limitPrice: {}, takeProfitPrice: {}, stopLossPrice: {}", 
                    request.getTicker(), request.getAmount(), request.getLimitPrice(), 
                    request.getTakeProfitPrice(), request.getStopLossPrice());

            // Get the current price of the stock
            BigDecimal currentPrice = getCurrentPrice(request.getTicker());

            // Calculate the quantity of shares to buy
            int quantity = calculateQuantity(request.getAmount(), currentPrice);

            if (quantity <= 0) {
                throw new IllegalArgumentException("Calculated quantity must be greater than zero. Please increase the dollar amount.");
            }

            log.info("Placing bracket order for ticker: {}, quantity: {} shares, limitPrice: {}", 
                    request.getTicker(), quantity, request.getLimitPrice());

            // In a real implementation, you would use the Alpaca API to place the order
            // For now, we'll just create a mock response

            // Generate mock order IDs
            String orderId = UUID.randomUUID().toString();
            String takeProfitOrderId = UUID.randomUUID().toString();
            String stopLossOrderId = UUID.randomUUID().toString();

            log.info("Order placed successfully. Order ID: {}", orderId);

            // Build and return response
            return BracketOrderResponse.builder()
                    .orderId(orderId)
                    .takeProfitOrderId(takeProfitOrderId)
                    .stopLossOrderId(stopLossOrderId)
                    .ticker(request.getTicker())
                    .amount(request.getAmount())
                    .quantity(quantity)
                    .status("ACCEPTED")
                    .build();

        } catch (Exception e) {
            log.error("Error placing bracket order: {}", e.getMessage());
            return BracketOrderResponse.builder()
                    .status("FAILED")
                    .errorMessage(e.getMessage())
                    .ticker(request.getTicker())
                    .amount(request.getAmount())
                    .build();
        }
    }
}
