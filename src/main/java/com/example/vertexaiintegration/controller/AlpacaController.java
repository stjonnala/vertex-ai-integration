package com.example.vertexaiintegration.controller;

import com.example.vertexaiintegration.model.OrderRequest;
import com.example.vertexaiintegration.model.OrderResponse;
import com.example.vertexaiintegration.service.AlpacaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alpaca")
@Tag(name = "Alpaca", description = "Alpaca Trading API")
public class AlpacaController {

    private static final Logger log = LoggerFactory.getLogger(AlpacaController.class);

    private final AlpacaService alpacaService;

    public AlpacaController(AlpacaService alpacaService) {
        this.alpacaService = alpacaService;
    }

    @Operation(summary = "Place a buy order", 
               description = "Places a market buy order in Alpaca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order placed successfully",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error or Alpaca API error")
    })
    @PostMapping("/buy")
    public ResponseEntity<OrderResponse> placeBuyOrder(@RequestBody OrderRequest request) {
        log.info("Received buy order request for ticker: {}, quantity: {}", request.getTicker(), request.getQuantity());
        
        // Validate request
        if (request.getTicker() == null || request.getTicker().isEmpty()) {
            return ResponseEntity.badRequest().body(
                new OrderResponse(
                    null,
                    request.getQuantity(),
                    "buy",
                    "FAILED",
                    "Ticker is required"
                )
            );
        }
        
        if (request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body(
                new OrderResponse(
                    request.getTicker(),
                    request.getQuantity(),
                    "buy",
                    "FAILED",
                    "Quantity must be greater than 0"
                )
            );
        }
        
        // Place the buy order
        OrderResponse response = alpacaService.placeBuyOrder(request);
        
        // Return appropriate response based on order status
        if ("FAILED".equals(response.getStatus())) {
            log.error("Failed to place buy order: {}", response.getErrorMessage());
            return ResponseEntity.status(500).body(response);
        }
        
        log.info("Buy order placed successfully. Order ID: {}", response.getOrderId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Place a sell order", 
               description = "Places a market sell order in Alpaca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order placed successfully",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error or Alpaca API error")
    })
    @PostMapping("/sell")
    public ResponseEntity<OrderResponse> placeSellOrder(@RequestBody OrderRequest request) {
        log.info("Received sell order request for ticker: {}, quantity: {}", request.getTicker(), request.getQuantity());
        
        // Validate request
        if (request.getTicker() == null || request.getTicker().isEmpty()) {
            return ResponseEntity.badRequest().body(
                new OrderResponse(
                    null,
                    request.getQuantity(),
                    "sell",
                    "FAILED",
                    "Ticker is required"
                )
            );
        }
        
        if (request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body(
                new OrderResponse(
                    request.getTicker(),
                    request.getQuantity(),
                    "sell",
                    "FAILED",
                    "Quantity must be greater than 0"
                )
            );
        }
        
        // Place the sell order
        OrderResponse response = alpacaService.placeSellOrder(request);
        
        // Return appropriate response based on order status
        if ("FAILED".equals(response.getStatus())) {
            log.error("Failed to place sell order: {}", response.getErrorMessage());
            return ResponseEntity.status(500).body(response);
        }
        
        log.info("Sell order placed successfully. Order ID: {}", response.getOrderId());
        return ResponseEntity.ok(response);
    }
}