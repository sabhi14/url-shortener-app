package com.abhishek.urlshortener.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for application health checks.
 * Used by load balancers, orchestration, or monitoring to verify the service is up.
 */
@RestController
@Tag(name = "Health", description = "Health check endpoints for monitoring and orchestration")
public class HealthController {

    @Operation(
            summary = "Check application health",
            description = "Returns the current health status of the application. Use this endpoint for load balancers, " +
                    "Kubernetes liveness/readiness probes, or monitoring tools to verify the service is running."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Service is up and running",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{\"status\": \"UP\"}")
                    )
            )
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
