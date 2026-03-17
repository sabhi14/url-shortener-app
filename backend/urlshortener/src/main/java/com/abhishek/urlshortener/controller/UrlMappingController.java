package com.abhishek.urlshortener.controller;

import com.abhishek.urlshortener.dto.CreateShortUrlRequest;
import com.abhishek.urlshortener.dto.CreateShortUrlResponse;
import com.abhishek.urlshortener.dto.FetchAllCreatedUrlsResponse;
import com.abhishek.urlshortener.entity.UrlMapping;
import com.abhishek.urlshortener.enums.UrlStatusFilter;
import com.abhishek.urlshortener.exception.InvalidUrlException;
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
import org.springframework.data.domain.Page;
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
@Tag(name = "URL Mapping", description = "Create short URLs from long URLs and manage short code mappings. " +
                "Use the shorten endpoint to get a short link for any valid URL.")
public class UrlMappingController {

        private final UrlMappingService urlMappingService;

        public UrlMappingController(UrlMappingService urlMappingService) {
                this.urlMappingService = urlMappingService;
        }

        @Operation(summary = "Create a short URL", description = "Accepts a long (original) URL and returns a unique short URL. "
                        +
                        "The short code is generated randomly and stored with the original URL. " +
                        "The response includes the short code, full short URL, and the original URL.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Short URL created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateShortUrlResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request - e.g. missing or invalid originalUrl, or URL validation failed")
        })
        @PostMapping("/shorten")
        public ResponseEntity<CreateShortUrlResponse> createShortUrl(
                        @Valid @RequestBody CreateShortUrlRequest request) {
                CreateShortUrlResponse response = urlMappingService.generateShortCode(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Redirect to original URL", description = "Resolves a short code and redirects (HTTP 302) to the original long URL. "
                        +
                        "On success, the response includes a Location header pointing to the original URL.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "302", description = "Redirect to the original URL (Location header contains the target)"),
                        @ApiResponse(responseCode = "404", description = "Short URL not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid short code format"),
                        @ApiResponse(responseCode = "410", description = "Short URL is inactive or is expired")
        })
        @GetMapping("/{shortCode}")
        public ResponseEntity<Map<String, Object>> redirectToOriginalUrl(
                        @Parameter(description = "Short code segment of the short URL (e.g. `aB3xY9`).", example = "aB3xY9") @PathVariable String shortCode) {
                if (!shortCode.matches("^[a-zA-Z0-9]+$")) {
                        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST,
                                "The short code must contain only letters and numbers");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
                }
                UrlMapping urlMapping = urlMappingService.resolveByShortCode(shortCode);
                return ResponseEntity.status(HttpStatus.FOUND)
                                .header("Location", urlMapping.getOriginalUrl())
                                .build();
        }

        @Operation(
                summary = "Fetch all created short URLs",
                description = "Returns a paginated list of short URLs. Optional params: status (ACTIVE, EXPIRED, or ALL; default ALL), page (0-based; default 0), and size (1-100; default 20). Sorted by createdAt descending."
        )
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Paginated list of short URLs", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FetchAllCreatedUrlsResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid query params: status, page, or size"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping
        public ResponseEntity<?> fetchAllCreatedUrls(
                        @Parameter(description = "Filter by status: ACTIVE, EXPIRED, or ALL", example = "ALL") @RequestParam(name = "status", required = false, defaultValue = "ALL") String statusParam,
                        @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                        @Parameter(description = "Page size (1-100)", example = "20") @RequestParam(name = "size", required = false, defaultValue = "20") int size) {
                UrlStatusFilter status;
                try {
                        status = UrlStatusFilter.valueOf(statusParam.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST,
                                "status must be one of: ACTIVE, EXPIRED, ALL");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
                }
                if (page < 0) {
                        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, "page must be >= 0");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
                }
                if (size < 1 || size > 100) {
                        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, "size must be between 1 and 100");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
                }
                Page<UrlMapping> pageResult = urlMappingService.fetchAllCreatedUrls(status, page, size);
                FetchAllCreatedUrlsResponse response = new FetchAllCreatedUrlsResponse(
                        pageResult.getContent(),
                        pageResult.getTotalElements(),
                        pageResult.getTotalPages(),
                        pageResult.getSize(),
                        pageResult.getNumber()
                );
                return ResponseEntity.ok(response);
        }

        @ExceptionHandler(ShortUrlNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleShortUrlNotFound(ShortUrlNotFoundException ex) {
                Map<String, Object> body = createErrorBody(HttpStatus.NOT_FOUND, ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }

        @ExceptionHandler(ShortUrlGoneException.class)
        public ResponseEntity<Map<String, Object>> handleShortUrlGone(ShortUrlGoneException ex) {
                Map<String, Object> body = createErrorBody(HttpStatus.GONE, ex.getMessage());
                return ResponseEntity.status(HttpStatus.GONE).body(body);
        }

        @ExceptionHandler(InvalidUrlException.class)
        public ResponseEntity<Map<String, Object>> handleInvalidUrl(InvalidUrlException ex) {
                Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        private Map<String, Object> createErrorBody(HttpStatus status, String message) {
                Map<String, Object> body = new HashMap<>();
                body.put("status", status.value());
                body.put("error", status.getReasonPhrase());
                body.put("message", message);
                return body;
        }
}
