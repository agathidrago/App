package com.example.app.service.impl;

import com.example.app.dto.BingSearchResponse;
import com.example.app.exception.SearchServiceException;
import com.example.app.service.ISearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BingSearchService implements ISearchService {

    @Value("${search.bing.apiKey}")
    private String bingApiKey;

    @Value("${search.bing.endpoint}")
    private String bingEndpoint;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ParallelSearchService parallelSearchService;

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
     * Retrieves the number of estimated matches for a single search word.
     *
     * @param word the search word for which to retrieve estimated matches
     * @return the estimated number of matches for the word
     * @throws SearchServiceException if an error occurs while constructing the request or fetching results
     */
    private long getHitsForSingleWord(String word) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Ocp-Apim-Subscription-Key", bingApiKey);
            URL url = new URL(bingEndpoint + "?q=" + URLEncoder.encode(word, "UTF-8"));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            log.info("Sending request to Bing API: {}", url);

            ResponseEntity<BingSearchResponse> response = restTemplate.exchange(
                    url.toString(),
                    HttpMethod.GET,
                    entity,
                    BingSearchResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && Objects.nonNull(response.getBody())) {
                long estimatedMatches = response.getBody().webPages().totalEstimatedMatches();
                log.info("Estimated matches for word '{}': {}", word, estimatedMatches);
                return estimatedMatches;
            } else {
                String errorMessage = response.getStatusCode() == HttpStatus.NOT_FOUND
                        ? "No results found for query: " + word
                        : "Failed to fetch results from Bing API for query: " + word;
                log.error(errorMessage);
                throw new SearchServiceException(errorMessage);
            }
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            log.error("Error constructing the request URL for word '{}': {}", word, e.getMessage(), e);
            throw new SearchServiceException("Error constructing the request URL: " + e.getMessage());
        }
    }
}
