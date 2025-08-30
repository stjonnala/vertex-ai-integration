package com.example.vertexaiintegration.controller;

import com.example.vertexaiintegration.model.Stock;
import com.example.vertexaiintegration.service.StockRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for stock recommendations based on Warren Buffett's investment strategy.
 */
@RestController
@RequestMapping("/api/stocks")
@Tag(name = "Stock Recommendations", description = "API for stock recommendations based on Warren Buffett's investment strategy")
@CrossOrigin(origins = "*") // Allow requests from any origin
public class StockRecommendationController {

    private static final Logger log = LoggerFactory.getLogger(StockRecommendationController.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private final StockRecommendationService stockRecommendationService;

    public StockRecommendationController(StockRecommendationService stockRecommendationService) {
        this.stockRecommendationService = stockRecommendationService;
    }

    @Operation(summary = "Get stock recommendations", 
               description = "Returns a list of stock recommendations based on Warren Buffett's investment strategy")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stock recommendations",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<List<Stock>> getStockRecommendations() {
        log.info("Received request for stock recommendations");
        List<Stock> recommendations = stockRecommendationService.getStockRecommendations();
        log.info("Returning {} stock recommendations", recommendations.size());
        return ResponseEntity.ok(recommendations);
    }

    @Operation(summary = "Get stock recommendations with metadata", 
               description = "Returns stock recommendations with metadata including last update time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved stock recommendations with metadata",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/with-metadata")
    public ResponseEntity<Map<String, Object>> getStockRecommendationsWithMetadata() {
        log.info("Received request for stock recommendations with metadata");
        
        List<Stock> recommendations = stockRecommendationService.getStockRecommendations();
        long lastUpdateTimestamp = stockRecommendationService.getLastUpdateTimestamp();
        
        Map<String, Object> response = new HashMap<>();
        response.put("recommendations", recommendations);
        response.put("count", recommendations.size());
        response.put("lastUpdated", lastUpdateTimestamp > 0 ? 
                FORMATTER.format(Instant.ofEpochMilli(lastUpdateTimestamp)) : "Never");
        
        log.info("Returning {} stock recommendations with metadata", recommendations.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update stock recommendations", 
               description = "Manually triggers an update of stock recommendations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully triggered stock recommendations update",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updateStockRecommendations() {
        log.info("Received request to update stock recommendations");
        
        // Trigger an update
        stockRecommendationService.updateStockRecommendations();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Stock recommendations update triggered");
        
        return ResponseEntity.ok(response);
    }
}