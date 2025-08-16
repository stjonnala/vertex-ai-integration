package com.example.vertexaiintegration.controller;

import com.example.vertexaiintegration.model.BracketOrderRequest;
import com.example.vertexaiintegration.model.BracketOrderResponse;
import com.example.vertexaiintegration.service.AlpacaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @GetMapping("/bracket-order")
    public ResponseEntity<BracketOrderResponse> placeBracketOrder(
            @Parameter(description = "Ticker symbol of the stock to trade", example = "AAPL", required = true)
            @RequestParam String ticker,

            @Parameter(description = "Dollar amount to invest (e.g., $3000, $4000)", example = "3000.00", required = true)
            @RequestParam BigDecimal amount,

            @Parameter(description = "Limit price for the entry order", example = "150.00", required = true)
            @RequestParam BigDecimal limitPrice,

            @Parameter(description = "Take profit price", example = "155.00", required = true)
            @RequestParam BigDecimal takeProfitPrice,

            @Parameter(description = "Stop loss price", example = "145.00", required = true)
            @RequestParam BigDecimal stopLossPrice) {

        log.info("Received bracket order request for ticker: {}", ticker);

        // Validate request
        if (ticker == null || ticker.isEmpty()) {
            return ResponseEntity.badRequest().body(
                BracketOrderResponse.builder()
                    .status("FAILED")
                    .errorMessage("Ticker is required")
                    .build()
            );
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(
                BracketOrderResponse.builder()
                    .status("FAILED")
                    .errorMessage("Amount must be greater than 0")
                    .build()
            );
        }

        // Create a BracketOrderRequest object from the parameters
        BracketOrderRequest request = new BracketOrderRequest(ticker, amount, limitPrice, takeProfitPrice, stopLossPrice);

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
    
    @PostMapping("/limit-order")
    @Operation(summary = "Place Limit Bracket Order with Cents-Based Profit and Loss", description = "Place a limit buy order with custom dollar amount, limit price, profit in cents above price, and loss in cents below price for a user-specified ticker")
    public String placeLimitFixedProfitBracketOrder(
            @Parameter(description = "Stock or cryptocurrency symbol", example = "AAPL", required = true) @RequestParam String ticker,
            @Parameter(description = "Dollar amount to invest", example = "3000.00", required = true) @RequestParam double amount,
            @Parameter(description = "Limit price for the buy order", example = "150.00", required = true) @RequestParam double limitPrice,
            @Parameter(description = "Cents above limit price for take profit", example = "50", required = true) @RequestParam double profitCents,
            @Parameter(description = "Cents below limit price for stop loss", example = "50", required = true) @RequestParam double lossCents) {

        // Calculate the number of shares to buy based on the amount and limit price
        int quantity = (int) Math.floor(amount / limitPrice);

        // Calculate take profit price by adding the profit cents to the limit price
        double takeProfitPrice = limitPrice + (profitCents / 100.0);

        // Calculate stop loss price by subtracting the loss cents from the limit price
        double stopLossPrice = limitPrice - (lossCents / 100.0);

        // Calculate the total profit and loss amounts for the service method
        double profitAmount = (takeProfitPrice - limitPrice) * quantity;
        double lossAmount = (limitPrice - stopLossPrice) * quantity;

        // Use the AlpacaService to place a limit bracket order with fixed profit and loss amounts
        return alpacaService.placeLimitBracketOrderWithFixedProfitLoss(ticker, amount, limitPrice, profitAmount, lossAmount);
    }
}