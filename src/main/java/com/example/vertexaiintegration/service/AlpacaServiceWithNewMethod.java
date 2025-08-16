package com.example.vertexaiintegration.service;

import com.example.vertexaiintegration.config.AlpacaConfig;
import com.example.vertexaiintegration.model.BracketOrderRequest;
import com.example.vertexaiintegration.model.BracketOrderResponse;
import net.jacobpeterson.alpaca.AlpacaAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AlpacaServiceWithNewMethod {

    private static final Logger log = LoggerFactory.getLogger(AlpacaServiceWithNewMethod.class);

    private final AlpacaAPI alpacaAPI;
    private final AlpacaConfig alpacaConfig;

    // Constructor
    public AlpacaServiceWithNewMethod(AlpacaAPI alpacaAPI, AlpacaConfig alpacaConfig) {
        this.alpacaAPI = alpacaAPI;
        this.alpacaConfig = alpacaConfig;
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

    /**
     * Place a bracket order with fixed profit and loss amounts using a limit buy order
     * This method uses the /v2/orders endpoint with order_class=bracket and type=limit
     *
     * @param symbol the stock symbol
     * @param amount the dollar amount to invest
     * @param limitPrice the limit price for the buy order
     * @param profitAmount the fixed dollar amount for take profit
     * @param lossAmount the fixed dollar amount for stop loss
     * @return a message indicating the result
     */
    public String placeLimitBracketOrderWithFixedProfitLoss(String symbol, double amount, double limitPrice, double profitAmount, double lossAmount) {
        log.info("Placing limit bracket order for {} with ${}, limit price ${}, ${} profit target, and ${} stop loss", 
                symbol, amount, limitPrice, profitAmount, lossAmount);

        try {
            // Create a RestTemplate for making HTTP requests
            RestTemplate restTemplate = new RestTemplate();

            // Set up the headers with authentication
            HttpHeaders headers = new HttpHeaders();
            headers.set("APCA-API-KEY-ID", alpacaConfig.getApiKey());
            headers.set("APCA-API-SECRET-KEY", alpacaConfig.getApiSecret());
            headers.set("Content-Type", "application/json");

            // Construct the URL for the orders endpoint
            String url = alpacaConfig.getBaseUrl() + "/v2/orders";

            // Get the current price for reference (not used for order placement)
            double currentPrice = getCurrentPrice(symbol).doubleValue();
            log.info("Current price for {} is ${}", symbol, currentPrice);

            // Calculate the number of shares to buy based on the amount and limit price
            int quantity = (int) Math.floor(amount / limitPrice);
            log.info("Calculated {} shares to buy based on ${} at limit price ${} per share", quantity, amount, limitPrice);

            // Calculate take profit price based on fixed profit amount
            // profit amount = (take profit price - limit price) * quantity
            // take profit price = limit price + (profit amount / quantity)
            double takeProfitPrice = limitPrice + (profitAmount / quantity);

            // Calculate stop loss price based on fixed loss amount
            // loss amount = (limit price - stop loss price) * quantity
            // stop loss price = limit price - (loss amount / quantity)
            double stopLossPrice = limitPrice - (lossAmount / quantity);

            // Create the request body for a bracket order
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("symbol", symbol.toUpperCase()); // Ensure symbol is uppercase
            requestBody.put("qty", quantity);
            requestBody.put("side", "buy");
            requestBody.put("type", "limit");
            requestBody.put("limit_price", String.format("%.2f", limitPrice));
            requestBody.put("time_in_force", "gtc"); // Good Till Canceled
            requestBody.put("order_class", "bracket");

            // Add take profit parameters
            Map<String, Object> takeProfit = new HashMap<>();
            takeProfit.put("limit_price", String.format("%.2f", takeProfitPrice));
            requestBody.put("take_profit", takeProfit);

            // Add stop loss parameters
            Map<String, Object> stopLoss = new HashMap<>();
            stopLoss.put("stop_price", String.format("%.2f", stopLossPrice));
            requestBody.put("stop_loss", stopLoss);

            // Create the HTTP entity with headers and body
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Make the HTTP request
            log.info("Sending limit bracket order request to Alpaca API: {}", requestBody);
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // Log the response
            log.info("Limit bracket order response: {}", response.getBody());

            // Extract the order ID from the response
            String orderId = response.getBody().get("id").toString();

            // Calculate profit and loss percentages for informational purposes
            double profitPercentage = (profitAmount / (limitPrice * quantity)) * 100;
            double lossPercentage = (lossAmount / (limitPrice * quantity)) * 100;

            // Calculate total investment
            double totalInvestment = limitPrice * quantity;

            return String.format("Successfully placed limit bracket order for %s with $%.2f investment.\n" +
                    "Current Price: $%.2f\n" +
                    "Limit Buy Price: $%.2f\n" +
                    "Shares Purchased: %d (approx. $%.2f)\n" +
                    "Take Profit Price: $%.2f ($%.2f profit, %.2f%% higher)\n" +
                    "Stop Loss Price: $%.2f ($%.2f loss, %.2f%% lower)\n" +
                    "Order ID: %s", 
                    symbol, amount, currentPrice, limitPrice, quantity, totalInvestment, takeProfitPrice, profitAmount, 
                    profitPercentage, stopLossPrice, lossAmount, lossPercentage, orderId);
        } catch (Exception e) {
            log.error("Error placing limit bracket order for {}: {}", symbol, e.getMessage(), e);
            return String.format("Failed to place limit bracket order for %s: %s", symbol, e.getMessage());
        }
    }
}