package com.example.app.service.impl;

import com.example.app.service.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service that aggregates search results from multiple search services.
 */
@Service
public class SearchAggregatorService {

    private final List<ISearchService> searchServices;

    /**
     * Injects a list of all beans that implement the ISearchService interface
     */
    @Autowired
    public SearchAggregatorService(List<ISearchService> searchServices) {
        this.searchServices = searchServices;
    }

    /**
     * Executes a search query across all registered search services and aggregates the results.
     *
     * @param query the search query to execute
     * @return a map containing the search service name and the corresponding number of hits
     */
    public Map<String, Long> search(String query) {
        Map<String, Long> results = new HashMap<>();

        for (ISearchService searchService : searchServices) {
            long hits = searchService.getSearchHits(query);
            results.put(searchService.getClass().getSimpleName(), hits);
        }

        return results;
    }
}
