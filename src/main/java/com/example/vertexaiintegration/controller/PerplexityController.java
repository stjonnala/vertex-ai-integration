package com.example.vertexaiintegration.controller;

import com.example.vertexaiintegration.model.PerplexityModel;
import com.example.vertexaiintegration.model.PerplexityRequest;
import com.example.vertexaiintegration.model.PerplexityResponse;
import com.example.vertexaiintegration.service.PerplexityService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/perplexity")
@Tag(name = "Perplexity", description = "Perplexity AI API")
public class PerplexityController {

    private static final Logger log = LoggerFactory.getLogger(PerplexityController.class);

    private final PerplexityService perplexityService;

    public PerplexityController(PerplexityService perplexityService) {
        this.perplexityService = perplexityService;
    }

    @Operation(summary = "Ask a question to Perplexity AI", 
               description = "Sends a question to Perplexity AI and returns the answer. Optionally specify a model to use.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question answered successfully",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = PerplexityResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error or Perplexity API error")
    })
    @PostMapping("/ask")
    public ResponseEntity<PerplexityResponse> askQuestion(@RequestBody PerplexityRequest request) {
        log.info("Received question for Perplexity AI: {}", request.getQuestion());

        // Validate request
        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            log.error("Question is empty or null");
            return ResponseEntity.badRequest().body(
                new PerplexityResponse("FAILED", "Question cannot be empty")
            );
        }

        // Log model information if provided
        if (request.getModel() != null && !request.getModel().trim().isEmpty()) {
            log.info("Using specified model: {}", request.getModel());
        }

        // Send the question to Perplexity AI
        PerplexityResponse response = perplexityService.query(request);

        // Return appropriate response based on status
        if ("FAILED".equals(response.getStatus())) {
            log.error("Failed to get answer from Perplexity AI: {}", response.getErrorMessage());
            return ResponseEntity.status(500).body(response);
        }

        log.info("Successfully received answer from Perplexity AI");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get available Perplexity AI models", 
               description = "Returns a list of available models that can be used with the Perplexity AI API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Models retrieved successfully")
    })
    @GetMapping("/models")
    public ResponseEntity<List<Map<String, String>>> getAvailableModels() {
        log.info("Retrieving available Perplexity AI models");

        List<Map<String, String>> models = Arrays.stream(PerplexityModel.values())
            .map(model -> {
                Map<String, String> modelInfo = new HashMap<>();
                modelInfo.put("id", model.getModelId());
                modelInfo.put("name", model.name());
                modelInfo.put("description", model.getDescription());
                return modelInfo;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(models);
    }

    @Operation(summary = "Ask a simple question to Perplexity AI", 
               description = "Sends a question to Perplexity AI and returns the answer. Uses the default model from configuration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question answered successfully",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = PerplexityResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error or Perplexity AI error")
    })
    @GetMapping("/ask-simple")
    public ResponseEntity<PerplexityResponse> askSimpleQuestion(@RequestParam String question) {
        log.info("Received simple question for Perplexity AI: {}", question);

        // Validate request
        if (question == null || question.trim().isEmpty()) {
            log.error("Question is empty or null");
            return ResponseEntity.badRequest().body(
                new PerplexityResponse("FAILED", "Question cannot be empty")
            );
        }

        // Send the question to Perplexity AI using the simplified method
        PerplexityResponse response = perplexityService.querySimple(question);

        // Return appropriate response based on status
        if ("FAILED".equals(response.getStatus())) {
            log.error("Failed to get answer from Perplexity AI: {}", response.getErrorMessage());
            return ResponseEntity.status(500).body(response);
        }

        log.info("Successfully received answer from Perplexity AI");
        return ResponseEntity.ok(response);
    }
}
