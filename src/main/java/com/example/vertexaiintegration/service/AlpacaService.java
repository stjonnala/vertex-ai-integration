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
}