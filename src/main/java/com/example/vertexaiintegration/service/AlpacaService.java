package com.example.vertexaiintegration.service;

import com.example.vertexaiintegration.config.AlpacaConfig;
import com.example.vertexaiintegration.model.OrderRequest;
import com.example.vertexaiintegration.model.OrderResponse;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.orders.Order;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderTimeInForce;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Service
public class AlpacaService {

    private static final Logger log = LoggerFactory.getLogger(AlpacaService.class);

    private final AlpacaAPI alpacaAPI;
    private final AlpacaConfig alpacaConfig;

    public AlpacaService(AlpacaAPI alpacaAPI, AlpacaConfig alpacaConfig) {
        this.alpacaAPI = alpacaAPI;
        this.alpacaConfig = alpacaConfig;
    }

    /**
     * Places a buy order in Alpaca.
     *
     * @param request The order request containing ticker and quantity
     * @return The order response
     */
    public OrderResponse placeBuyOrder(OrderRequest request) {
        log.info("Placing buy order for ticker: {}, quantity: {}", request.getTicker(), request.getQuantity());

        try {
            // Create and place the order
            Order order = alpacaAPI.orders().requestMarketOrder(
                    request.getTicker(),
                    request.getQuantity(),
                    OrderSide.BUY,
                    OrderTimeInForce.DAY
            );

            log.info("Buy order placed successfully. Order ID: {}", order.getId());

            // Create and return the response
            return new OrderResponse(
                    order.getId(),
                    request.getTicker(),
                    request.getQuantity(),
                    "buy",
                    order.getStatus().toString()
            );
        } catch (Exception e) {
            log.error("Error placing buy order: {}", e.getMessage(), e);
            return new OrderResponse(
                    request.getTicker(),
                    request.getQuantity(),
                    "buy",
                    "FAILED",
                    e.getMessage()
            );
        }
    }

    /**
     * Places a sell order in Alpaca.
     *
     * @param request The order request containing ticker and quantity
     * @return The order response
     */
    public OrderResponse placeSellOrder(OrderRequest request) {
        log.info("Placing sell order for ticker: {}, quantity: {}", request.getTicker(), request.getQuantity());

        try {
            // Create and place the order
            Order order = alpacaAPI.orders().requestMarketOrder(
                    request.getTicker(),
                    request.getQuantity(),
                    OrderSide.SELL,
                    OrderTimeInForce.DAY
            );

            log.info("Sell order placed successfully. Order ID: {}", order.getId());

            // Create and return the response
            return new OrderResponse(
                    order.getId(),
                    request.getTicker(),
                    request.getQuantity(),
                    "sell",
                    order.getStatus().toString()
            );
        } catch (Exception e) {
            log.error("Error placing sell order: {}", e.getMessage(), e);
            return new OrderResponse(
                    request.getTicker(),
                    request.getQuantity(),
                    "sell",
                    "FAILED",
                    e.getMessage()
            );
        }
    }

    /**
     * Gets the latest price for a given ticker symbol using a public API.
     * 
     * Note: This implementation uses the Alpha Vantage API to fetch real-time stock prices.
     * Alpha Vantage provides free stock APIs but has rate limits (especially with the demo key).
     * In a production environment, you should:
     * 1. Register for a free API key at https://www.alphavantage.co/support/#api-key
     * 2. Store the API key in application.properties
     * 3. Consider implementing caching to reduce API calls
     * 4. Handle rate limiting gracefully
     *
     * @param ticker The ticker symbol to get the price for
     * @return The latest price, or -1 if the price could not be retrieved
     */
    public double getLatestPrice(String ticker) {
        log.info("Getting latest price for ticker: {}", ticker);

        try {
            // Use Alpha Vantage's Global Quote API to get the latest price
            // Note: The demo key has very limited API calls per day
            // For production use, replace with your own API key
            String apiKey = "demo"; // Replace with your Alpha Vantage API key
            String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + ticker + "&apikey=" + apiKey;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("Global Quote")) {
                Map<String, Object> quote = (Map<String, Object>) responseBody.get("Global Quote");
                if (quote != null && quote.containsKey("05. price")) {
                    String priceStr = (String) quote.get("05. price");
                    double price = Double.parseDouble(priceStr);
                    log.info("Latest price for {}: ${}", ticker, price);
                    return price;
                }
            }

            log.warn("No price data available for ticker: {}", ticker);
            return -1;
        } catch (Exception e) {
            log.error("Unexpected error getting latest price for ticker {}: {}", ticker, e.getMessage(), e);
            return -1;
        }
    }
}
