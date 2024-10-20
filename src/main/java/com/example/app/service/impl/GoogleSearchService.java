package com.example.app.service.impl;

import com.example.app.dto.GoogleSearchResponse;
import com.example.app.exception.SearchServiceException;
import com.example.app.service.ISearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class GoogleSearchService implements ISearchService {

    @Value("${search.google.apiKey}")
    private String googleApiKey;

    @Value("${search.google.engine.id}")
    private String googleSearchEngineId;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ParallelSearchService parallelSearchService;

    private static final String GOOGLE_SEARCH_URL =
            "https://www.googleapis.com/customsearch/v1?key={key}&cx={cx}&q={query}";

    /**
     * Retrieves the total number of search hits for the given query.
     *
     * @param query the search query for which to retrieve hits
     * @return the total number of search hits
     * @throws SearchServiceException if the query is null or empty
     */

    @Override
    public long getSearchHits(String query) {

        log.info("Received search query: {}", query);

        if (query == null || query.trim().isEmpty()) {
            log.error("Search query cannot be null or empty");
            throw new SearchServiceException("Search query cannot be null or empty");
        }

        List<String> searchWords = List.of(query.split("[\\s,]+"));
        List<Long> hitsList = parallelSearchService.executeParallelSearch(searchWords, this::getHitsForSingleWord);

        long totalHits = hitsList.stream().mapToLong(Long::longValue).sum();
        log.info("Total search hits found: {}", totalHits);
        return totalHits;
    }

    /**
     * Retrieves the number of total results for a single search word.
     *
     * @param word the search word for which to retrieve total results
     * @return the total number of results for the word
     * @throws SearchServiceException if an error occurs while fetching results
     */
    private long getHitsForSingleWord(String word) {

        Map<String, String> params = new HashMap<>();
        params.put("key", googleApiKey);
        params.put("cx", googleSearchEngineId);
        params.put("query", word);

        try {
            ResponseEntity<GoogleSearchResponse> response = restTemplate.getForEntity(GOOGLE_SEARCH_URL, GoogleSearchResponse.class, params);

            if (response.getStatusCode() == HttpStatus.OK && Objects.nonNull(response.getBody())) {
                GoogleSearchResponse searchResponse = response.getBody();
                long totalResults = searchResponse.searchInformation().totalResults();
                log.info("Total results for word '{}': {}", word, totalResults);
                return totalResults;
            } else {
                String errorMessage = response.getStatusCode() == HttpStatus.NOT_FOUND
                        ? "No results found for query: " + word
                        : "Failed to fetch results from Google API for query: " + word;
                log.error(errorMessage);
                throw new SearchServiceException(errorMessage);
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching hits for word '{}': {}", word, e.getMessage(), e);
            throw new SearchServiceException("Error fetching results for word: " + word, e);
        }
    }
}
