package com.abhishek.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for creating a new short URL.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a short URL from a long URL")
public class CreateShortUrlRequest {

    @Schema(
            description = "The original (long) URL to shorten. Must be a valid URL.",
            example = "https://example.com/very/long/path",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 2048
    )
    @NotBlank(message = "Original URL is required")
    @Size(max = 2048, message = "Original URL must be less than 2048 characters")
    private String originalUrl;
}
