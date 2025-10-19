package com.hieunguyen.podcastai.service.impl;

import com.hieunguyen.podcastai.dto.response.NewsApiResponse;
import com.hieunguyen.podcastai.entity.NewsArticle;
import com.hieunguyen.podcastai.entity.NewsSource;
import com.hieunguyen.podcastai.enums.NewsSourceType;
import com.hieunguyen.podcastai.service.NewsSourceIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsApiIntegrationService implements NewsSourceIntegrationService {
    
    private final RestTemplate restTemplate;
    
    private static final String EVERYTHING_ENDPOINT = "/everything";
    private static final String TOP_HEADLINES_ENDPOINT = "/top-headlines";
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    @Override
    @Retryable(value = {RestClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<NewsArticle> fetchNews(NewsSource source) {
        log.info("Fetching news from News API: {}", source.getName());
        
        List<NewsArticle> allArticles = new ArrayList<>();
        
        try {
            // 1. Fetch from /everything endpoint
            NewsApiResponse everythingResponse = fetchEverything(source);
            List<NewsArticle> everythingArticles = convertToArticles(everythingResponse, source);
            allArticles.addAll(everythingArticles);
            
            // 2. Fetch from /top-headlines endpoint
            NewsApiResponse headlinesResponse = fetchTopHeadlines(source);
            List<NewsArticle> headlinesArticles = convertToArticles(headlinesResponse, source);
            allArticles.addAll(headlinesArticles);
            
            // 3. Remove duplicates
            List<NewsArticle> uniqueArticles = removeDuplicates(allArticles);
            
            log.info("Fetched {} unique articles from News API", uniqueArticles.size());
            return uniqueArticles;
            
        } catch (Exception e) {
            log.error("Failed to fetch from News API: {}", e.getMessage(), e);
            throw new RuntimeException("News API fetch failed", e);
        }
    }
    
    private NewsApiResponse fetchEverything(NewsSource source) {
        try {
            URI uri = buildEverythingUri(source);
            log.debug("Requesting News API with URI: {}", uri);
            
            NewsApiResponse response = restTemplate.getForObject(uri, NewsApiResponse.class);
            
            if (response == null) {
                log.error("News API returned null response");
                return createErrorResponse("No response from News API");
            }
            
            if (!"ok".equals(response.getStatus())) {
                log.error("News API returned error status: {}, message: {}", 
                         response.getStatus(), response.getMessage());
                return response;
            }
            
            log.info("Successfully retrieved {} articles from News API", 
                    response.getArticles() != null ? response.getArticles().size() : 0);
            
            return response;
            
        } catch (RestClientException e) {
            log.error("Failed to fetch everything from News API", e);
            throw new RuntimeException("Failed to fetch everything: " + e.getMessage(), e);
        }
    }
    
    private NewsApiResponse fetchTopHeadlines(NewsSource source) {
        try {
            URI uri = buildTopHeadlinesUri(source);
            log.debug("Requesting News API with URI: {}", uri);
            
            NewsApiResponse response = restTemplate.getForObject(uri, NewsApiResponse.class);
            
            if (response == null) {
                log.error("News API returned null response");
                return createErrorResponse("No response from News API");
            }
            
            if (!"ok".equals(response.getStatus())) {
                log.error("News API returned error status: {}, message: {}", 
                         response.getStatus(), response.getMessage());
                return response;
            }
            
            log.info("Successfully retrieved {} headlines from News API", 
                    response.getArticles() != null ? response.getArticles().size() : 0);
            
            return response;
            
        } catch (RestClientException e) {
            log.error("Failed to fetch top headlines from News API", e);
            throw new RuntimeException("Failed to fetch top headlines: " + e.getMessage(), e);
        }
    }
    
    private URI buildEverythingUri(NewsSource source) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(source.getUrl() + EVERYTHING_ENDPOINT);
        
        Map<String, Object> params = new HashMap<>();
        params.put("apiKey", source.getApiKey());
        params.put("q", "technology");
        params.put("language", source.getLanguage() != null ? source.getLanguage() : "en");
        params.put("sortBy", "publishedAt");
        params.put("pageSize", source.getMaxArticlesPerFetch() != null ? source.getMaxArticlesPerFetch() : 100);
        params.put("page", 1);
        
        if (source.getDescription() != null && source.getDescription().contains("technology")) {
            params.put("q", "technology");
        }
        
        params.put("from", LocalDateTime.now().minusDays(7).format(ISO_FORMATTER));
        
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        
        return builder.build().toUri();
    }
    
    private URI buildTopHeadlinesUri(NewsSource source) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(source.getUrl() + TOP_HEADLINES_ENDPOINT);
        
        Map<String, Object> params = new HashMap<>();
        
        params.put("apiKey", source.getApiKey());
        params.put("country", source.getCountry() != null ? source.getCountry() : "us");
        params.put("category", "technology");
        params.put("language", source.getLanguage() != null ? source.getLanguage() : "en");
        params.put("pageSize", source.getMaxArticlesPerFetch() != null ? source.getMaxArticlesPerFetch() : 100);
        params.put("page", 1);
        
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        
        return builder.build().toUri();
    }
    
    
    private List<NewsArticle> convertToArticles(NewsApiResponse response, NewsSource source) {
        if (response == null || response.getArticles() == null) {
            return new ArrayList<>();
        }
        
        return response.getArticles().stream()
            .map(articleDto -> {
                NewsArticle.NewsArticleBuilder builder = NewsArticle.builder()
                    .title(articleDto.getTitle())
                    .description(articleDto.getDescription())
                    .content(articleDto.getContent())
                    .url(articleDto.getUrl())
                    .sourceName(articleDto.getSource().getName())
                    .sourceUrl(articleDto.getSource().getUrl())
                    .author(articleDto.getAuthor())
                    .imageUrl(articleDto.getUrlToImage())
                    .language(source.getLanguage() != null ? source.getLanguage() : "en")
                    .newsSource(source);
                
                // Xử lý publishedAt nếu có
                if (articleDto.getPublishedAt() != null) {
                    try {
                        builder.publishedAt(java.time.Instant.parse(articleDto.getPublishedAt()));
                    } catch (Exception e) {
                        log.warn("Failed to parse publishedAt: {}", articleDto.getPublishedAt());
                    }
                }
                
                return builder.build();
            })
            .collect(Collectors.toList());
    }
    
    private List<NewsArticle> removeDuplicates(List<NewsArticle> articles) {
        return articles.stream()
            .collect(Collectors.toMap(
                NewsArticle::getUrl,
                article -> article,
                (existing, replacement) -> existing
            ))
            .values()
            .stream()
            .collect(Collectors.toList());
    }
    
    private NewsApiResponse createErrorResponse(String message) {
        return NewsApiResponse.builder()
                .status("error")
                .message(message)
                .totalResults(0)
                .articles(null)
                .build();
    }
    
    @Override
    public boolean isHealthy() {
        try {
            // Tạo NewsSource test tạm thời
            NewsSource testSource = NewsSource.builder()
                    .name("test")
                    .language("en")
                    .maxArticlesPerFetch(1)
                    .build();
            
            NewsApiResponse response = fetchEverything(testSource);
            return response != null && "ok".equals(response.getStatus());
            
        } catch (Exception e) {
            log.error("News API health check failed", e);
            return false;
        }
    }
    
    @Override
    public String getSourceType() {
        return NewsSourceType.NEWS_API.name();
    }
}