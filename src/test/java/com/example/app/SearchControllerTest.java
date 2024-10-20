package com.example.app;

import com.example.app.controller.SearchController;
import com.example.app.service.impl.SearchAggregatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class SearchControllerTest {

    @Mock
    private SearchAggregatorService searchAggregatorService;

    @InjectMocks
    private SearchController searchController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void search_ShouldReturnResults_ForValidInput() {
        String searchWords = "java";
        Map<String, Long> expectedResults = new HashMap<>();
        expectedResults.put("BingSearchService", 100L);
        expectedResults.put("GoogleSearchService", 200L);

        when(searchAggregatorService.search(searchWords)).thenReturn(expectedResults);

        ResponseEntity<Map<String, Long>> response = searchController.search(searchWords);

        assertEquals(ResponseEntity.ok(expectedResults), response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void search_ShouldThrowError_ForEmptyQuery() {
        String searchWords = "";

        try {
            searchController.search(searchWords);
        } catch (IllegalArgumentException e) {
            assertEquals("Search query cannot be null or empty", e.getMessage());
        }
    }

    @Test
    void search_ShouldThrowError_ForNullQuery() {
        String searchWords = null;

        try {
            searchController.search(searchWords);
        } catch (IllegalArgumentException e) {
            assertEquals("Search query cannot be null or empty", e.getMessage());
        }
    }

    @Test
    void search_ShouldReturnPartialResults_WhenOneServiceFails() {
        String searchWords = "java";
        Map<String, Long> partialResults = new HashMap<>();
        partialResults.put("BingSearchService", 100L);

        when(searchAggregatorService.search(searchWords)).thenReturn(partialResults);

        ResponseEntity<Map<String, Long>> response = searchController.search(searchWords);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(partialResults, response.getBody());
    }

    @Test
    void search_ShouldReturnZeroResults_ForQueryWithNoMatches() {
        String searchWords = "asdasdasdasdasd";
        Map<String, Long> noResults = new HashMap<>();
        noResults.put("BingSearchService", 0L);
        noResults.put("GoogleSearchService", 0L);

        when(searchAggregatorService.search(searchWords)).thenReturn(noResults);

        ResponseEntity<Map<String, Long>> response = searchController.search(searchWords);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(noResults, response.getBody());
    }

    @Test
    void search_ShouldReturnResults_ForMultipleWordsQuery() {
        String searchWords = "java spring";
        Map<String, Long> expectedResults = new HashMap<>();
        expectedResults.put("BingSearchService", 300L);
        expectedResults.put("GoogleSearchService", 400L);

        when(searchAggregatorService.search(searchWords)).thenReturn(expectedResults);

        ResponseEntity<Map<String, Long>> response = searchController.search(searchWords);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResults, response.getBody());
    }
}
