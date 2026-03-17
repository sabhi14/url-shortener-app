package com.abhishek.urlshortener.dto;

import com.abhishek.urlshortener.entity.UrlMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Paginated response for the fetch-all short URLs API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paginated list of short URLs with metadata")
public class FetchAllCreatedUrlsResponse {

    @Schema(description = "Page of URL mappings")
    private List<UrlMapping> content;

    @Schema(description = "Total number of elements across all pages")
    private long totalElements;

    @Schema(description = "Total number of pages")
    private int totalPages;

    @Schema(description = "Page size")
    private int size;

    @Schema(description = "Current page number (0-based)")
    private int number;
}
