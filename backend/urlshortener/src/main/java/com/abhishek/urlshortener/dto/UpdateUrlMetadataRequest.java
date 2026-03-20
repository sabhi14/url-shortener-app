package com.abhishek.urlshortener.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for updating URL metadata.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for updating URL metadata")
public class UpdateUrlMetadataRequest {

    @Schema(description = "Updated original URL", example = "https://example.com/updated/path")
    private String originalUrl;

    @Schema(description = "Updated custom alias", example = "custom-alias")
    private String customAlias;

    @Schema(description = "Updated expiration date-time in ISO format", example = "2026-03-30T10:15:30")
    private LocalDateTime expiresAt;

    @Schema(description = "Whether the short URL is active", example = "true")
    private Boolean active;
}
