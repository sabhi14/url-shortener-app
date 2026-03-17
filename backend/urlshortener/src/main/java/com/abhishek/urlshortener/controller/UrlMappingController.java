package com.abhishek.urlshortener.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abhishek.urlshortener.dto.AnalyticsResponse;
import com.abhishek.urlshortener.dto.CreateShortUrlRequest;
import com.abhishek.urlshortener.dto.CreateShortUrlResponse;
import com.abhishek.urlshortener.dto.FetchAllCreatedUrlsResponse;
import com.abhishek.urlshortener.entity.UrlMapping;
import com.abhishek.urlshortener.enums.UrlStatusFilter;
import com.abhishek.urlshortener.service.UrlMappingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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

        @Operation(summary = "Fetch all created short URLs", description = "Returns a paginated list of short URLs. Optional params: status (ACTIVE, EXPIRED, or ALL; default ALL), page (0-based; default 0), and size (1-100; default 20). Sorted by createdAt descending.")
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
                Map<String, Object> body = new HashMap<>();
                try {
                        status = UrlStatusFilter.valueOf(statusParam.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                        body.put("status", HttpStatus.BAD_REQUEST.value());
                        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
                        body.put("message", "status must be one of: ACTIVE, EXPIRED, ALL");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
                }
                if (page < 0) {
                        body.put("status", HttpStatus.BAD_REQUEST.value());
                        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
                        body.put("message", "page must be >= 0");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
                }
                if (size < 1 || size > 100) {
                        body.put("status", HttpStatus.BAD_REQUEST.value());
                        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
                        body.put("message", "size must be between 1 and 100");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
                }
                Page<UrlMapping> pageResult = urlMappingService.fetchAllCreatedUrls(status, page, size);
                FetchAllCreatedUrlsResponse response = new FetchAllCreatedUrlsResponse(
                                pageResult.getContent(),
                                pageResult.getTotalElements(),
                                pageResult.getTotalPages(),
                                pageResult.getSize(),
                                pageResult.getNumber());
                return ResponseEntity.ok(response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get a short URL by ID", description = "Returns a short URL by its ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Short URL found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlMapping.class))),
                        @ApiResponse(responseCode = "404", description = "Short URL not found")
        })

        public ResponseEntity<UrlMapping> getUrlMappingById(@PathVariable Long id) {
                UrlMapping urlMapping = urlMappingService.getUrlMappingById(id);
                return ResponseEntity.ok(urlMapping);
        }

        @Operation(summary = "Delete a short URL by ID", description = "Deletes a short URL by its ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Short URL deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Short URL not found")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteUrlMappingById(@PathVariable Long id) {
                urlMappingService.deleteUrlMappingById(id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/{id}/analytics")
        @Operation(summary = "Get analytics for a short URL by ID", description = "Returns analytics for a short URL by its ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Analytics found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnalyticsResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Analytics not found")
        })
        public ResponseEntity<AnalyticsResponse> getAnalyticsById(@PathVariable Long id) {
                AnalyticsResponse response = urlMappingService.getAnalyticsById(id);
                return ResponseEntity.ok(response);

        }

}
