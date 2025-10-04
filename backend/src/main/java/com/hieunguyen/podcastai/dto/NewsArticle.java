package com.hieunguyen.podcastai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for news article from News API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsArticle {
    
    @JsonProperty("source")
    private NewsSource source;
    
    @JsonProperty("author")
    private String author;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("urlToImage")
    private String urlToImage;
    
    @JsonProperty("publishedAt")
    private LocalDateTime publishedAt;
    
    @JsonProperty("content")
    private String content;
    
    // Additional fields for processing
    private String fullContent; // Full content after crawling
    private String processedContent; // Content after summarization/translation
    private Double relevanceScore; // Relevance score for ranking
    private Integer wordCount; // Word count for content length
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NewsSource {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("name")
        private String name;
    }
}

