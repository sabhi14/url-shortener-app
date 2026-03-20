package com.abhishek.urlshortener.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response returned after a short URL is successfully created")
public class AnalyticsResponse {
    @Schema(description = "The ID of the short URL")
    private Long id;
    @Schema(description = "The original long URL that was shortened")
    private String originalUrl;
    @Schema(description = "The short code (path segment) used in the short URL")
    private String shortUrl;
    @Schema(description = "The date and time the short URL was created")
    private LocalDateTime createdAt;
    @Schema(description = "The date and time the short URL expires")
    private LocalDateTime expiresAt;
    @Schema(description = "The number of times the short URL has been clicked")
    private Long clickCount;
    @Schema(description = "Whether the short URL is active")
    private boolean active;
}
