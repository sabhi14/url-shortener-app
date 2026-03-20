package com.abhishek.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response returned after successfully creating a short URL.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response returned after a short URL is successfully created")
public class CreateShortUrlResponse {

    @Schema(description = "The original long URL that was shortened")
    private String originalUrl;

    @Schema(description = "The short code (path segment) used in the short URL", example = "aB3xY9")
    private String shortCode;

    @Schema(description = "The full short URL to use for redirects", example = "http://localhost:8080/aB3xY9")
    private String shortUrl;
}
