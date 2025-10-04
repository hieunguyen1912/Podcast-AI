package com.hieunguyen.podcastai.service.impl;

import com.hieunguyen.podcastai.config.NewsApiConfig;
import com.hieunguyen.podcastai.dto.NewsApiResponse;
import com.hieunguyen.podcastai.dto.NewsArticle;
import com.hieunguyen.podcastai.service.NewsContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of NewsContentService for News API integration
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NewsContentServiceImpl implements NewsContentService {
    
    private final NewsApiConfig newsApiConfig;
    
    @Qualifier("newsApiRestTemplate")
    private final RestTemplate restTemplate;
    
    @Override
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<NewsArticle> searchNews(String query) {
        return searchNews(query, newsApiConfig.getDefaultLanguage(), 
                         newsApiConfig.getDefaultSortBy(), newsApiConfig.getMaxArticles());
    }
    
    @Override
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<NewsArticle> searchNews(String query, String language, String sortBy, int pageSize) {
        try {
            log.info("Searching news for query: {}, language: {}, sortBy: {}, pageSize: {}", 
                    query, language, sortBy, pageSize);
            
            // Build URL for everything endpoint
            String url = buildEverythingUrl(query, language, sortBy, pageSize);
            
            // Call News API
            NewsApiResponse response = restTemplate.getForObject(url, NewsApiResponse.class);
            
            if (response == null || !"ok".equals(response.getStatus())) {
                log.error("News API returned error: {}", response != null ? response.getMessage() : "null response");
                return Collections.emptyList();
            }
            
            List<NewsArticle> articles = response.getArticles();
            if (articles == null || articles.isEmpty()) {
                log.warn("No articles found for query: {}", query);
                return Collections.emptyList();
            }
            
            // Process articles
            articles = processArticles(articles);
            
            // Filter and rank articles
            articles = filterAndRankArticles(articles, newsApiConfig.getMaxArticles());
            
            log.info("Found {} articles for query: {}", articles.size(), query);
            return articles;
            
        } catch (Exception e) {
            log.error("Failed to search news for query: {}", query, e);
            throw new RuntimeException("Failed to search news", e);
        }
    }
    
    @Override
    @Retryable(value = {Exception.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public String getArticleContent(String articleUrl) {
        try {
            log.debug("Fetching full content for URL: {}", articleUrl);
        
            // Connect to the URL with timeout
            Document doc = Jsoup.connect(articleUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000) // 10 seconds timeout
                .followRedirects(true)
                .get();
            
            // Remove unwanted elements
            doc.select("script, style, nav, header, footer, aside, .advertisement, .ads").remove();
            
            // Try different selectors for article content
            String content = extractContent(doc);
            
            if (content == null || content.trim().isEmpty()) {
                log.warn("No content found for URL: {}", articleUrl);
                return "Content not available";
            }
            
            // Clean and limit content
            content = cleanContent(content);
            log.info("Successfully extracted {} characters from URL: {}", content.length(), articleUrl);
            
            return content;
            
        } catch (Exception e) {
            log.error("Failed to get article content for URL: {}", articleUrl, e);
            return "Content extraction failed";
        }
    }

    private String extractContent(Document doc) {
        // Try common article selectors
        String[] selectors = {
            "article",
            "[role='main']",
            ".article-content",
            ".post-content", 
            ".entry-content",
            ".content",
            ".story-body",
            ".article-body",
            "main",
            ".main-content"
        };
        
        for (String selector : selectors) {
            Elements elements = doc.select(selector);
            if (!elements.isEmpty()) {
                String content = elements.first().text();
                if (content.length() > 200) { // Minimum content length
                    return content;
                }
            }
        }

        // Fallback: get all paragraphs
        Elements paragraphs = doc.select("p");
        if (!paragraphs.isEmpty()) {
            return paragraphs.stream()
                .map(Element::text)
                .filter(text -> text.length() > 50)
                .collect(Collectors.joining(" "));
        }
        
        return null;
    }

    private String cleanContent(String content) {
        if (content == null) return "";
        
        // Remove extra whitespace
        content = content.replaceAll("\\s+", " ").trim();
        
        // Limit content length (max 10,000 characters)
        if (content.length() > 10000) {
            content = content.substring(0, 10000) + "...";
        }
        
        return content;
    }
    
    @Override
    public List<NewsArticle> getTrendingNews(String category) {
        return getTopHeadlines("us", category, newsApiConfig.getMaxArticles());
    }
    
    @Override
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<NewsArticle> getTopHeadlines(String country, String category, int pageSize) {
        try {
            log.info("Getting top headlines for country: {}, category: {}, pageSize: {}", 
                    country, category, pageSize);
            
            // Build URL for top headlines endpoint
            String url = buildTopHeadlinesUrl(country, category, pageSize);
            
            // Call News API
            NewsApiResponse response = restTemplate.getForObject(url, NewsApiResponse.class);
            
            if (response == null || !"ok".equals(response.getStatus())) {
                log.error("News API returned error: {}", response != null ? response.getMessage() : "null response");
                return Collections.emptyList();
            }
            
            List<NewsArticle> articles = response.getArticles();
            if (articles == null || articles.isEmpty()) {
                log.warn("No headlines found for country: {}, category: {}", country, category);
                return Collections.emptyList();
            }
            
            // Process articles
            articles = processArticles(articles);
            
            log.info("Found {} headlines for country: {}, category: {}", articles.size(), country, category);
            return articles;
            
        } catch (Exception e) {
            log.error("Failed to get top headlines for country: {}, category: {}", country, category, e);
            throw new RuntimeException("Failed to get top headlines", e);
        }
    }
    
    @Override
    public String processNewsContent(List<NewsArticle> articles, int maxLength) {
        if (articles == null || articles.isEmpty()) {
            return "No news articles found.";
        }
        
        StringBuilder content = new StringBuilder();
        content.append("Here are the latest news updates:\n\n");
        
        for (int i = 0; i < articles.size() && content.length() < maxLength; i++) {
            NewsArticle article = articles.get(i);
            
            content.append("Article ").append(i + 1).append(":\n");
            content.append("Title: ").append(article.getTitle()).append("\n");
            
            if (article.getSource() != null && article.getSource().getName() != null) {
                content.append("Source: ").append(article.getSource().getName()).append("\n");
            }
            
            if (article.getPublishedAt() != null) {
                content.append("Published: ").append(article.getPublishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n");
            }
            
            content.append("Summary: ").append(article.getDescription() != null ? article.getDescription() : "No description available").append("\n");
            
            if (article.getFullContent() != null && !article.getFullContent().isEmpty()) {
                content.append("Content: ").append(article.getFullContent()).append("\n");
            }
            
            content.append("\n");
        }
        
        // Truncate if too long
        if (content.length() > maxLength) {
            content.setLength(maxLength);
            content.append("...");
        }
        
        return content.toString();
    }
    
    @Override
    public List<NewsArticle> filterAndRankArticles(List<NewsArticle> articles, int maxArticles) {
        if (articles == null || articles.isEmpty()) {
            return Collections.emptyList();
        }
        
        return articles.stream()
            .filter(this::isValidArticle)
            .peek(this::calculateRelevanceScore)
            .sorted((a, b) -> Double.compare(
                b.getRelevanceScore() != null ? b.getRelevanceScore() : 0.0,
                a.getRelevanceScore() != null ? a.getRelevanceScore() : 0.0
            ))
            .limit(maxArticles)
            .collect(Collectors.toList());
    }
    
    // Private helper methods
    
    private String buildEverythingUrl(String query, String language, String sortBy, int pageSize) {
        return UriComponentsBuilder.fromUriString(newsApiConfig.getBaseUrl() + "/everything")
            .queryParam("q", URLEncoder.encode(query, StandardCharsets.UTF_8))
            .queryParam("language", language)
            .queryParam("sortBy", sortBy)
            .queryParam("pageSize", Math.min(pageSize, 100))
            .queryParam("apiKey", newsApiConfig.getKey())
            .build()
            .toUriString();
    }
    
    private String buildTopHeadlinesUrl(String country, String category, int pageSize) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(newsApiConfig.getBaseUrl() + "/top-headlines")
            .queryParam("pageSize", Math.min(pageSize, 100))
            .queryParam("apiKey", newsApiConfig.getKey());
        
        if (country != null && !country.isEmpty()) {
            builder.queryParam("country", country);
        }
        
        if (category != null && !category.isEmpty()) {
            builder.queryParam("category", category);
        }
        
        return builder.build().toUriString();
    }
    
    private List<NewsArticle> processArticles(List<NewsArticle> articles) {
        return articles.stream()
            .peek(article -> {
                // Set word count
                if (article.getDescription() != null) {
                    article.setWordCount(article.getDescription().split("\\s+").length);
                }
                
                // Get full content (placeholder implementation)
                if (article.getUrl() != null) {
                    article.setFullContent(getArticleContent(article.getUrl()));
                }
            })
            .collect(Collectors.toList());
    }
    
    private boolean isValidArticle(NewsArticle article) {
        return article != null 
            && article.getTitle() != null && !article.getTitle().trim().isEmpty()
            && article.getDescription() != null && !article.getDescription().trim().isEmpty()
            && article.getUrl() != null && !article.getUrl().trim().isEmpty();
    }
    
    private void calculateRelevanceScore(NewsArticle article) {
        double score = 0.0;
        
        // Base score
        score += 1.0;
        
        // Title quality (length and content)
        if (article.getTitle() != null) {
            score += Math.min(article.getTitle().length() / 100.0, 2.0);
        }
        
        // Description quality
        if (article.getDescription() != null) {
            score += Math.min(article.getDescription().length() / 200.0, 2.0);
        }
        
        // Recency (newer articles get higher scores)
        if (article.getPublishedAt() != null) {
            long hoursSincePublished = java.time.Duration.between(article.getPublishedAt(), LocalDateTime.now()).toHours();
            score += Math.max(0, 3.0 - (hoursSincePublished / 24.0));
        }
        
        // Source credibility
        if (article.getSource() != null && article.getSource().getName() != null) {
            String sourceName = article.getSource().getName().toLowerCase();
            if (sourceName.contains("bbc") || sourceName.contains("cnn") || sourceName.contains("reuters")) {
                score += 1.0;
            }
        }
        
        article.setRelevanceScore(score);
    }
    
    // Recovery methods for retry
    @Recover
    public List<NewsArticle> recoverSearchNews(Exception ex, String query) {
        log.warn("Using fallback for search query: {}", query);
        return getFallbackNews(query);
    }
    
    @Recover
    public List<NewsArticle> recoverSearchNews(Exception ex, String query, String language, String sortBy, int pageSize) {
        log.warn("Using fallback for search query: {}", query);
        return getFallbackNews(query);
    }
    
    @Recover
    public List<NewsArticle> recoverTopHeadlines(Exception ex, String country, String category, int pageSize) {
        log.warn("Using fallback for top headlines");
        return getFallbackHeadlines();
    }
    
    private List<NewsArticle> getFallbackNews(String query) {
        // Return a fallback article when News API fails
        NewsArticle fallbackArticle = NewsArticle.builder()
            .title("News Service Temporarily Unavailable")
            .description("We're experiencing technical difficulties. Please try again later.")
            .source(NewsArticle.NewsSource.builder().name("System").build())
            .publishedAt(LocalDateTime.now())
            .relevanceScore(0.5)
            .build();
        
        return Collections.singletonList(fallbackArticle);
    }
    
    private List<NewsArticle> getFallbackHeadlines() {
        // Return fallback headlines
        NewsArticle fallbackArticle = NewsArticle.builder()
            .title("Top Headlines Service Temporarily Unavailable")
            .description("We're experiencing technical difficulties. Please try again later.")
            .source(NewsArticle.NewsSource.builder().name("System").build())
            .publishedAt(LocalDateTime.now())
            .relevanceScore(0.5)
            .build();
        
        return Collections.singletonList(fallbackArticle);
    }
}
