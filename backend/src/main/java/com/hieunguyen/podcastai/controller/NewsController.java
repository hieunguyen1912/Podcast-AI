package com.hieunguyen.podcastai.controller;

import com.hieunguyen.podcastai.entity.NewsArticle;
import com.hieunguyen.podcastai.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/news")
@Slf4j
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /**
     * Get all news articles with pagination and sorting
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting news articles - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);
        
        try {
            Page<NewsArticle> articlesPage = newsService.getAllNews(page, size, sortBy, sortDir);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("articles", articlesPage.getContent());
            response.put("totalElements", articlesPage.getTotalElements());
            response.put("totalPages", articlesPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            response.put("hasNext", articlesPage.hasNext());
            response.put("hasPrevious", articlesPage.hasPrevious());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get news articles: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get news articles: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get news article by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getNewsById(@PathVariable Long id) {
        log.info("Getting news article by ID: {}", id);
        
        try {
            return newsService.getNewsById(id)
                .map(article -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("article", article);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
            
        } catch (Exception e) {
            log.error("Failed to get news article {}: {}", id, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get news article: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Search news articles by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchNews(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Searching news articles with keyword: {}", keyword);
        
        try {
            Page<NewsArticle> articlesPage = newsService.searchNews(keyword, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("articles", articlesPage.getContent());
            response.put("totalElements", articlesPage.getTotalElements());
            response.put("totalPages", articlesPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            response.put("keyword", keyword);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to search news articles: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to search news articles: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get trending news (most viewed)
     */
    @GetMapping("/trending")
    public ResponseEntity<Map<String, Object>> getTrendingNews(
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Getting trending news - limit: {}", limit);
        
        try {
            List<NewsArticle> articles = newsService.getTrendingNews(limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("articles", articles);
            response.put("count", articles.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get trending news: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get trending news: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get latest news
     */
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestNews(
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Getting latest news - limit: {}", limit);
        
        try {
            List<NewsArticle> articles = newsService.getLatestNews(limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("articles", articles);
            response.put("count", articles.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get latest news: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get latest news: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get news by category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getNewsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting news by category ID: {}", categoryId);
        
        try {
            Page<NewsArticle> articlesPage = newsService.getNewsByCategory(categoryId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("articles", articlesPage.getContent());
            response.put("totalElements", articlesPage.getTotalElements());
            response.put("totalPages", articlesPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            response.put("categoryId", categoryId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get news by category {}: {}", categoryId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get news by category: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get news by source
     */
    @GetMapping("/source/{sourceId}")
    public ResponseEntity<Map<String, Object>> getNewsBySource(
            @PathVariable Long sourceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting news by source ID: {}", sourceId);
        
        try {
            Page<NewsArticle> articlesPage = newsService.getNewsBySource(sourceId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("articles", articlesPage.getContent());
            response.put("totalElements", articlesPage.getTotalElements());
            response.put("totalPages", articlesPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            response.put("sourceId", sourceId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get news by source {}: {}", sourceId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get news by source: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get related articles
     */
    @GetMapping("/{id}/related")
    public ResponseEntity<Map<String, Object>> getRelatedArticles(
            @PathVariable Long id,
            @RequestParam(defaultValue = "5") int limit) {
        
        log.info("Getting related articles for: {}", id);
        
        try {
            List<NewsArticle> articles = newsService.getRelatedArticles(id, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("articles", articles);
            response.put("count", articles.size());
            response.put("articleId", id);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get related articles for {}: {}", id, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get related articles: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Like an article
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likeArticle(@PathVariable Long id) {
        log.info("Liking article: {}", id);
        
        try {
            boolean success = newsService.likeArticle(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Article liked successfully" : "Article not found");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to like article {}: {}", id, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to like article: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Share an article
     */
    @PostMapping("/{id}/share")
    public ResponseEntity<Map<String, Object>> shareArticle(@PathVariable Long id) {
        log.info("Sharing article: {}", id);
        
        try {
            boolean success = newsService.shareArticle(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Article shared successfully" : "Article not found");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to share article {}: {}", id, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to share article: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get news statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getNewsStatistics() {
        log.info("Getting news statistics");
        
        try {
            NewsService.NewsStatistics stats = newsService.getNewsStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("statistics", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get news statistics: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get news statistics: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}