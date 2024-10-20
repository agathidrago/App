package com.example.app.dto;

/**
 * DTO for Google Search API response.
 */
public record GoogleSearchResponse(SearchInformation searchInformation) {

    public record SearchInformation(long totalResults) {
    }
}
