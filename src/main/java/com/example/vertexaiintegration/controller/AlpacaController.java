package com.example.vertexaiintegration.controller;

import com.example.vertexaiintegration.model.BracketOrderRequest;
import com.example.vertexaiintegration.model.BracketOrderResponse;
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

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/alpaca")
@Tag(name = "Alpaca", description = "Alpaca Trading API")
public class AlpacaController {

    private static final Logger log = LoggerFactory.getLogger(AlpacaController.class);

    private final AlpacaService alpacaService;

    // Constructor
    public AlpacaController(AlpacaService alpacaService) {
        this.alpacaService = alpacaService;
    }

    @Operation(summary = "Place a bracket order", 
               description = "Places a bracket order in Alpaca with a main order, take profit, and stop loss")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order placed successfully",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = BracketOrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error or Alpaca API error")
    })
    @PostMapping("/bracket-order")
    public ResponseEntity<BracketOrderResponse> placeBracketOrder(@RequestBody BracketOrderRequest request) {
        log.info("Received bracket order request for ticker: {}", request.getTicker());

        // Validate request
        if (request.getTicker() == null || request.getTicker().isEmpty()) {
            return ResponseEntity.badRequest().body(
                BracketOrderResponse.builder()
                    .status("FAILED")
                    .errorMessage("Ticker is required")
                    .build()
            );
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(
                BracketOrderResponse.builder()
                    .status("FAILED")
                    .errorMessage("Amount must be greater than 0")
                    .build()
            );
        }

        // Place the bracket order
        BracketOrderResponse response = alpacaService.placeBracketOrder(request);

        // Return appropriate response based on order status
        if ("FAILED".equals(response.getStatus())) {
            log.error("Failed to place bracket order: {}", response.getErrorMessage());
            return ResponseEntity.status(500).body(response);
        }

        log.info("Bracket order placed successfully. Order ID: {}", response.getOrderId());
        return ResponseEntity.ok(response);
    }
}
