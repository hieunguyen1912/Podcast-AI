package com.hieunguyen.podcastai.service.impl;

import com.hieunguyen.podcastai.dto.response.NewsApiResponse;
import com.hieunguyen.podcastai.entity.NewsArticle;
import com.hieunguyen.podcastai.entity.NewsSource;
import com.hieunguyen.podcastai.enums.FetchType;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;

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
        if (source.getFetchConfigurations() == null) return allArticles;
        for (var fetchConfig : source.getFetchConfigurations()) {
            if (fetchConfig.getEnabled()) {
                NewsApiResponse apiResponse = null;
                if (fetchConfig.getFetchType() != null && fetchConfig.getFetchType().equals(FetchType.SEARCH)) {
                    apiResponse = fetchEverything(source, fetchConfig);
                } else {
                    apiResponse = fetchTopHeadlines(source, fetchConfig);
                }
                if (apiResponse != null && apiResponse.getArticles() != null) {
                    var articles = convertToArticles(apiResponse, fetchConfig);
                    allArticles.addAll(articles);
                }
            }
        }
        return removeDuplicates(allArticles);
    }
    
    private NewsApiResponse fetchEverything(NewsSource source, com.hieunguyen.podcastai.entity.FetchConfiguration fetchConfiguration) {
        try {
            URI uri = buildEverythingUri(source, fetchConfiguration);
            log.debug("Requesting News API /everything with URI: {}", uri);
            ResponseEntity<NewsApiResponse> response = restTemplate.exchange(uri, org.springframework.http.HttpMethod.GET, null, NewsApiResponse.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("News API everything trả về lỗi. Code: {}, uri: {}", response.getStatusCode(), uri);
                return createErrorResponse("No response from News API");
            }
            return response.getBody();
        } catch (org.springframework.web.client.HttpStatusCodeException httpEx) {
            int status = httpEx.getStatusCode().value();
            String errorBody = httpEx.getResponseBodyAsString();
            log.error("News API everything error: status {}, body {}", status, errorBody);
            return createErrorResponse("Error status: " + status);
        } catch (RestClientException e) {
            log.error("Failed to fetch everything from News API", e);
            return createErrorResponse("Failed request: " + e.getMessage());
        }
    }

    private NewsApiResponse fetchTopHeadlines(NewsSource source, com.hieunguyen.podcastai.entity.FetchConfiguration fetchConfiguration) {
        try {
            URI uri = buildTopHeadlinesUri(source, fetchConfiguration);
            log.debug("Requesting News API /top-headlines with URI: {}", uri);
            ResponseEntity<NewsApiResponse> response = restTemplate.exchange(uri, org.springframework.http.HttpMethod.GET, null, NewsApiResponse.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("News API top-headlines trả về lỗi. Code: {}, uri: {}", response.getStatusCode(), uri);
                return createErrorResponse("No response from News API");
            }
            return response.getBody();
        } catch (org.springframework.web.client.HttpStatusCodeException httpEx) {
            int status = httpEx.getStatusCode().value();
            String errorBody = httpEx.getResponseBodyAsString();
            log.error("News API top-headlines error: status {}, body {}", status, errorBody);
            return createErrorResponse("Error status: " + status);
        } catch (RestClientException e) {
            log.error("Failed to fetch top headlines from News API", e);
            return createErrorResponse("Failed request: " + e.getMessage());
        }
    }

    private URI buildEverythingUri(NewsSource source, com.hieunguyen.podcastai.entity.FetchConfiguration fetchConfiguration) {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(source.getApiBaseUrl() + EVERYTHING_ENDPOINT);
        builder.queryParam("apiKey", source.getApiKey());
        builder.queryParam("q", fetchConfiguration.getKeywords());
        builder.queryParam("language", fetchConfiguration.getLanguages());
        builder.queryParam("sortBy", fetchConfiguration.getSortBy());
        builder.queryParam("pageSize", fetchConfiguration.getMaxResults());
        builder.queryParam("page", 1);
        builder.queryParam("from", fetchConfiguration.getFrom());
        builder.queryParam("to", fetchConfiguration.getTo());
        return builder.build().toUri();
    }

    private URI buildTopHeadlinesUri(NewsSource source, com.hieunguyen.podcastai.entity.FetchConfiguration fetchConfiguration) {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(source.getApiBaseUrl() + TOP_HEADLINES_ENDPOINT);
        builder.queryParam("apiKey", source.getApiKey());
        builder.queryParam("country", fetchConfiguration.getCountries());
        builder.queryParam("category", (fetchConfiguration.getCategory() != null && fetchConfiguration.getCategory().getName() != null) ? fetchConfiguration.getCategory().getName() : "");
        builder.queryParam("language", fetchConfiguration.getLanguages());
        builder.queryParam("pageSize", fetchConfiguration.getMaxResults());
        builder.queryParam("page", 1);
        builder.queryParam("q", fetchConfiguration.getKeywords());
        return builder.build().toUri();
    }

    private List<NewsArticle> convertToArticles(NewsApiResponse response, com.hieunguyen.podcastai.entity.FetchConfiguration fetchConfiguration) {
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
                    .sourceName(articleDto.getSource() != null ? articleDto.getSource().getName() : null)
                    .author(articleDto.getAuthor())
                    .imageUrl(articleDto.getUrlToImage())
                    .category(fetchConfiguration.getCategory())
                    .sources(fetchConfiguration.getNewsSource());
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
                    .apiBaseUrl("https://newsapi.org/v2")
                    .apiKey("591345a44bad433cbb40718ded78128d")
                    .type(NewsSourceType.NEWS_API)
                    .isActive(true)
                    .build();
            
            NewsApiResponse response = fetchEverything(testSource, null); // Pass null for fetchConfig
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