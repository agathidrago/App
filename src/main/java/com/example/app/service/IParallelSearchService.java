package com.example.app.service;

import com.example.app.service.impl.ParallelSearchService;

import java.util.List;

public interface IParallelSearchService {

    List<Long> executeParallelSearch(List<String> searchWords, ParallelSearchService.SearchQueryFunction searchQueryFunction);
}
