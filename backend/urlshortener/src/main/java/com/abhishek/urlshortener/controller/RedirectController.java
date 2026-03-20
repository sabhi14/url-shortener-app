package com.abhishek.urlshortener.controller;

import com.abhishek.urlshortener.entity.UrlMapping;
import com.abhishek.urlshortener.service.UrlMappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Redirect", description = "Resolve short codes and redirect to original URLs.")
public class RedirectController {

    private final UrlMappingService urlMappingService;

    public RedirectController(UrlMappingService urlMappingService) {
        this.urlMappingService = urlMappingService;
    }

    @Operation(summary = "Redirect to original URL", description = "Resolves a short code and redirects (HTTP 302) to the original long URL. "
            + "On success, the response includes a Location header pointing to the original URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirect to the original URL (Location header contains the target)"),
            @ApiResponse(responseCode = "404", description = "Short URL not found"),
            @ApiResponse(responseCode = "400", description = "Invalid short code format"),
            @ApiResponse(responseCode = "410", description = "Short URL is inactive or is expired")
    })
    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirectToOriginalUrl(
            @Parameter(description = "Short code segment of the short URL (e.g. `aB3xY9`).", example = "aB3xY9") @PathVariable String shortCode) {
        if (!shortCode.matches("^[a-zA-Z0-9]+$")) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
            body.put("message", "The short code must contain only letters and numbers");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        UrlMapping urlMapping = urlMappingService.resolveByShortCode(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", urlMapping.getOriginalUrl())
                .build();
    }
}
