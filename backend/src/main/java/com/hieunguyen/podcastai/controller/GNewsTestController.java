package com.hieunguyen.podcastai.controller;

import com.hieunguyen.podcastai.dto.response.ApiResponse;
import com.hieunguyen.podcastai.dto.response.NewsArticleResponse;
import com.hieunguyen.podcastai.entity.NewsSource;
import com.hieunguyen.podcastai.enums.ErrorCode;
import com.hieunguyen.podcastai.enums.NewsSourceType;
import com.hieunguyen.podcastai.exception.AppException;
import com.hieunguyen.podcastai.mapper.NewsArticleMapper;
import com.hieunguyen.podcastai.repository.NewsSourceRepository;
import com.hieunguyen.podcastai.service.NewsSourceIntegrationService;
import com.hieunguyen.podcastai.service.NewsSourceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test/gnews")
@RequiredArgsConstructor
@Slf4j
public class GNewsTestController {

    private final NewsSourceRepository newsSourceRepository;
    private final NewsSourceFactory newsSourceFactory;
    private final NewsArticleMapper newsArticleMapper;

    @PostMapping("/fetch/{sourceId}")
    public ResponseEntity<ApiResponse<List<NewsArticleResponse>>> testFetchNews(@PathVariable Long sourceId) {
        log.info("Testing GNews fetch for source ID: {}", sourceId);

        // Lấy NewsSource từ database
        NewsSource newsSource = newsSourceRepository.findById(sourceId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // Kiểm tra loại source có phải GNews không
        if (!newsSource.getType().equals(NewsSourceType.GNEWS_API)) {
            log.error("Source type mismatch. Expected GNEWS_API, got: {}", newsSource.getType());
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }

        // Kiểm tra source có active không
        if (!newsSource.getIsActive()) {
            log.error("News source {} is not active", newsSource.getName());
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }

        // Tạo service và fetch news
        NewsSourceIntegrationService integrationService = newsSourceFactory.createService(newsSource);
        
        if (integrationService == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        try {
            // Fetch news từ GNews API
            var articles = integrationService.fetchNews(newsSource);
            
            log.info("Fetched {} articles from GNews", articles != null ? articles.size() : 0);

            // Convert to DTO
            List<NewsArticleResponse> response = newsArticleMapper.toDtoList(articles);

            return ResponseEntity.ok(ApiResponse.success(
                    String.format("Successfully fetched %d articles from %s", 
                            response.size(), newsSource.getName()), 
                    response));
                    
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching news from GNews: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @GetMapping("/health/{sourceId}")
    public ResponseEntity<ApiResponse<Boolean>> checkHealth(@PathVariable Long sourceId) {
        log.info("Checking GNews health for source ID: {}", sourceId);

        NewsSource newsSource = newsSourceRepository.findById(sourceId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!newsSource.getType().equals(NewsSourceType.GNEWS_API)) {
            log.error("Source type mismatch. Expected GNEWS_API, got: {}", newsSource.getType());
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }

        NewsSourceIntegrationService integrationService = newsSourceFactory.createService(newsSource);
        
        if (integrationService == null) {
            return ResponseEntity.ok(ApiResponse.success("Service is not available", false));
        }

        boolean isHealthy = integrationService.isHealthy();
        return ResponseEntity.ok(ApiResponse.success(
                isHealthy ? "GNews service is healthy" : "GNews service is not healthy", 
                isHealthy));
    }

    @GetMapping("/source-type/{sourceId}")
    public ResponseEntity<ApiResponse<String>> getSourceType(@PathVariable Long sourceId) {
        log.info("Getting source type for source ID: {}", sourceId);

        NewsSource newsSource = newsSourceRepository.findById(sourceId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        NewsSourceIntegrationService integrationService = newsSourceFactory.createService(newsSource);
        
        if (integrationService == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        String sourceType = integrationService.getSourceType();
        return ResponseEntity.ok(ApiResponse.success("Source type retrieved", sourceType));
    }
}
