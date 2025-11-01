package com.hieunguyen.podcastai.controller;

import com.hieunguyen.podcastai.entity.NewsArticle;
import com.hieunguyen.podcastai.entity.NewsSource;
import com.hieunguyen.podcastai.enums.NewsSourceType;
import com.hieunguyen.podcastai.repository.NewsArticleRepository;
import com.hieunguyen.podcastai.repository.NewsSourceRepository;
import com.hieunguyen.podcastai.service.NewsAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/news/test")
@Slf4j
@RequiredArgsConstructor
public class NewsTestController {

    private final NewsAggregationService newsAggregationService;
    private final NewsArticleRepository newsArticleRepository;
    private final NewsSourceRepository newsSourceRepository;

    /**
     * Test fetch news from all active sources
     */
    @PostMapping("/fetch-all")
    public ResponseEntity<Map<String, Object>> fetchAllNews() {
        log.info("Testing fetch all news from all sources");
        
        try {
            int articlesCount = newsAggregationService.fetchAllNews();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully fetched news from all sources");
            response.put("articlesCount", articlesCount);
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to fetch all news: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch news: " + e.getMessage());
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Test fetch news from specific source by ID
     */
    @PostMapping("/fetch-source/{sourceId}")
    public ResponseEntity<Map<String, Object>> fetchNewsFromSource(@PathVariable Long sourceId) {
        log.info("Testing fetch news from source ID: {}", sourceId);
        
        try {
            int articlesCount = newsAggregationService.fetchNewsFromSource(sourceId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully fetched news from source ID: " + sourceId);
            response.put("articlesCount", articlesCount);
            response.put("sourceId", sourceId);
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to fetch news from source {}: {}", sourceId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch news from source: " + e.getMessage());
            response.put("sourceId", sourceId);
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Test fetch news from sources by type
     */
    @PostMapping("/fetch-type/{sourceType}")
    public ResponseEntity<Map<String, Object>> fetchNewsFromType(@PathVariable NewsSourceType sourceType) {
        log.info("Testing fetch news from source type: {}", sourceType);
        
        try {
            int articlesCount = newsAggregationService.fetchNewsFromSourceType(sourceType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully fetched news from source type: " + sourceType);
            response.put("articlesCount", articlesCount);
            response.put("sourceType", sourceType);
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to fetch news from type {}: {}", sourceType, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch news from type: " + e.getMessage());
            response.put("sourceType", sourceType);
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get all news articles with pagination
     */

    /**
     * Get all news sources
     */
    @GetMapping("/sources")
    public ResponseEntity<Map<String, Object>> getAllSources() {
        log.info("Getting all news sources");
        
        try {
            List<NewsSource> sources = newsSourceRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sources", sources);
            response.put("count", sources.size());
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get sources: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get sources: " + e.getMessage());
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get active news sources only
     */
    @GetMapping("/sources/active")
    public ResponseEntity<Map<String, Object>> getActiveSources() {
        log.info("Getting active news sources");
        
        try {
            List<NewsSource> sources = newsSourceRepository.findByIsActiveTrue();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sources", sources);
            response.put("count", sources.size());
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get active sources: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get active sources: " + e.getMessage());
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Health check for all sources
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        log.info("Checking health of all news sources");
        
        try {
            boolean isHealthy = newsAggregationService.checkAllSourcesHealth();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("healthy", isHealthy);
            response.put("message", isHealthy ? "All sources are healthy" : "Some sources are unhealthy");
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("healthy", false);
            response.put("message", "Health check failed: " + e.getMessage());
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get statistics about news data
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting news statistics");
        
        try {
            long totalArticles = newsArticleRepository.count();
            long totalSources = newsSourceRepository.count();
            long activeSources = newsSourceRepository.findByIsActiveTrue().size();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalArticles", totalArticles);
            response.put("totalSources", totalSources);
            response.put("activeSources", activeSources);
            response.put("inactiveSources", totalSources - activeSources);
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get stats: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get stats: " + e.getMessage());
            response.put("timestamp", java.time.Instant.now());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}