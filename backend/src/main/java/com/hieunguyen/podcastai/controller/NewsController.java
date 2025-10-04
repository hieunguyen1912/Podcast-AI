package com.hieunguyen.podcastai.controller;

import com.hieunguyen.podcastai.dto.NewsArticle;
import com.hieunguyen.podcastai.dto.request.ProcessNewsRequest;
import com.hieunguyen.podcastai.dto.response.ApiResponse;
import com.hieunguyen.podcastai.service.NewsContentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for News API operations
 */
@RestController
@RequestMapping("/api/v1/news")
@Slf4j
@RequiredArgsConstructor
@Validated
public class NewsController {
    
    private final NewsContentService newsContentService;
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<NewsArticle>>> searchNews(
            @RequestParam @NotBlank(message = "Query cannot be blank") String query,
            @RequestParam(defaultValue = "en") @Size(min = 2, max = 5, message = "Language code must be 2-5 characters") String language,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") 
            @Max(value = 100, message = "Page size cannot exceed 100") int pageSize) {
        
        log.info("Searching news for query: {}, language: {}, sortBy: {}, pageSize: {}", 
                query, language, sortBy, pageSize);
        
        try {
            List<NewsArticle> articles = newsContentService.searchNews(query, language, sortBy, pageSize);
            return ResponseEntity.ok(ApiResponse.success("News search completed successfully", articles));
        } catch (Exception e) {
            log.error("Failed to search news for query: {}", query, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to search news: " + e.getMessage()));
        }
    }
    
    @GetMapping("/trending")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<NewsArticle>>> getTrendingNews(
            @RequestParam(defaultValue = "technology") String category) {
        
        log.info("Getting trending news for category: {}", category);
        
        try {
            List<NewsArticle> articles = newsContentService.getTrendingNews(category);
            return ResponseEntity.ok(ApiResponse.success("Trending news retrieved successfully", articles));
        } catch (Exception e) {
            log.error("Failed to get trending news for category: {}", category, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get trending news: " + e.getMessage()));
        }
    }
    
    @GetMapping("/headlines")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<NewsArticle>>> getTopHeadlines(
            @RequestParam(defaultValue = "us") @Size(min = 2, max = 2, message = "Country code must be 2 characters") String country,
            @RequestParam(defaultValue = "technology") String category,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") 
            @Max(value = 100, message = "Page size cannot exceed 100") int pageSize) {
        
        log.info("Getting top headlines for country: {}, category: {}, pageSize: {}", 
                country, category, pageSize);
        
        try {
            List<NewsArticle> articles = newsContentService.getTopHeadlines(country, category, pageSize);
            return ResponseEntity.ok(ApiResponse.success("Top headlines retrieved successfully", articles));
        } catch (Exception e) {
            log.error("Failed to get top headlines for country: {}, category: {}", country, category, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get top headlines: " + e.getMessage()));
        }
    }
    
    @GetMapping("/content")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> getArticleContent(
            @RequestParam @NotBlank(message = "Article URL cannot be blank") String url) {
        
        log.info("Getting article content for URL: {}", url);
        
        try {
            String content = newsContentService.getArticleContent(url);
            return ResponseEntity.ok(ApiResponse.success("Article content retrieved successfully", content));
        } catch (Exception e) {
            log.error("Failed to get article content for URL: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get article content: " + e.getMessage()));
        }
    }
    
    @PostMapping("/process")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> processNewsContent(@Valid @RequestBody ProcessNewsRequest request) {
        
        log.info("Processing news content for podcast generation: {}", request);
        
        try {
            // Search for news articles
            List<NewsArticle> articles = newsContentService.searchNews(
                    request.getQuery(), 
                    request.getLanguage(), 
                    request.getSortBy(), 
                    request.getMaxArticles()
            );
            
            // Process and combine content
            String processedContent = newsContentService.processNewsContent(articles, request.getMaxLength());
            
            return ResponseEntity.ok(ApiResponse.success("News content processed successfully", processedContent));
        } catch (Exception e) {
            log.error("Failed to process news content: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to process news content: " + e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        log.info("News API health check requested");
        
        try {
            // Test with a simple query
            List<NewsArticle> testArticles = newsContentService.searchNews("test", "en", "publishedAt", 1);
            return ResponseEntity.ok(ApiResponse.success("News API is healthy", 
                    "Found " + testArticles.size() + " test articles"));
        } catch (Exception e) {
            log.error("News API health check failed", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error("News API is not available: " + e.getMessage()));
        }
    }
    
}