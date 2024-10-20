package com.example.app.dto;

/**
 * DTO for Bing Search API response.
 */
public record BingSearchResponse(WebPages webPages) {

    public record WebPages(long totalEstimatedMatches) {
    }
}
