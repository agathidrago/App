package com.example.app.controller;

import com.example.app.service.impl.SearchAggregatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchAggregatorService searchAggregatorService;

    @PostMapping("/get-hit-number")
    public ResponseEntity<Map<String, Long>> search(@RequestParam String searchWords) {
        Map<String, Long> result = searchAggregatorService.search(searchWords);
        return ResponseEntity.ok(result);
    }
}
