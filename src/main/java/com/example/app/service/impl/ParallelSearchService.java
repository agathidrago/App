package com.example.app.service.impl;

import com.example.app.service.IParallelSearchService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class ParallelSearchService implements IParallelSearchService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    /**
     * Executes a parallel search for the given list of search words using the provided search query function.
     *
     * @param searchWords         the list of words to search for
     * @param searchQueryFunction the function that defines how to get search hits for each word
     * @return a list of total search hits for each word
     */
    @Override
    public List<Long> executeParallelSearch(List<String> searchWords, SearchQueryFunction searchQueryFunction) {
        // Create a CompletableFuture for each word search
        List<CompletableFuture<Long>> futures = searchWords.stream()
                .map(word -> CompletableFuture.supplyAsync(() -> searchQueryFunction.getSearchHitsForWord(word), executorService))
                .toList();

        // Wait for all requests to complete and return the results
        return futures.stream()
                .map(CompletableFuture::join)  // Wait for each future to complete
                .collect(Collectors.toList());
    }

    /**
     * Functional interface to define custom search behavior for fetching search hits for a word.
     */
    @FunctionalInterface
    public interface SearchQueryFunction {
        long getSearchHitsForWord(String word);
    }
}
