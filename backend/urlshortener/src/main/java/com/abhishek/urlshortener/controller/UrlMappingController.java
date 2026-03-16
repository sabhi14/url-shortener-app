package com.abhishek.urlshortener.controller;

import com.abhishek.urlshortener.dto.CreateShortUrlRequest;
import com.abhishek.urlshortener.dto.CreateShortUrlResponse;
import com.abhishek.urlshortener.entity.UrlMapping;
import com.abhishek.urlshortener.exception.ShortUrlNotFoundException;
import com.abhishek.urlshortener.exception.ShortUrlGoneException;
import com.abhishek.urlshortener.service.UrlMappingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for creating and resolving short URLs.
 */
@RestController
@RequestMapping("/api/urls")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(
        name = "URL Mapping",
        description = "Create short URLs from long URLs and manage short code mappings. " +
                "Use the shorten endpoint to get a short link for any valid URL."
)
public class UrlMappingController {

    @Autowired
    private UrlMappingService urlMappingService;

    @Operation(
            summary = "Create a short URL",
            description = "Accepts a long (original) URL and returns a unique short URL. " +
                    "The short code is generated randomly and stored with the original URL. " +
                    "The response includes the short code, full short URL, and the original URL."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Short URL created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateShortUrlResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - e.g. missing or invalid originalUrl, or URL validation failed"
            )
    })
    @PostMapping("/shorten")
    public ResponseEntity<CreateShortUrlResponse> createShortUrl(@Valid @RequestBody CreateShortUrlRequest request) {
        CreateShortUrlResponse response = urlMappingService.generateShortCode(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Redirect to original URL",
            description = "Resolves a short code and redirects (HTTP 302) to the original long URL. " +
                    "On success, the response includes a Location header pointing to the original URL."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Redirect to the original URL (Location header contains the target)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Short URL not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid short code format"
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Short URL is inactive or is expired"
            )
    })
    @GetMapping("/{shortCode}")
    public ResponseEntity<Map<String, Object>> redirectToOriginalUrl(
            @Parameter(
                    description = "Short code segment of the short URL (e.g. `aB3xY9`).",
                    example = "aB3xY9"
            )
            @PathVariable String shortCode) {
        if (!shortCode.matches("^[a-zA-Z0-9]+$")) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "Bad Request");
            body.put("message", "The short code must contain only letters and numbers");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }
        UrlMapping urlMapping = urlMappingService.resolveByShortCode(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", urlMapping.getOriginalUrl())
                .build();
    }

    @ExceptionHandler(ShortUrlNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleShortUrlNotFound(ShortUrlNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
    @ExceptionHandler(ShortUrlGoneException.class)
    public ResponseEntity<Map<String, Object>> handleShortUrlGone(ShortUrlGoneException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.GONE.value());
        body.put("error", "Gone");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GONE).body(body);
    }
}
