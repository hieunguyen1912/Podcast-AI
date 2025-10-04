package com.hieunguyen.podcastai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for News API response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsApiResponse {
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("totalResults")
    private Integer totalResults;
    
    @JsonProperty("articles")
    private List<NewsArticle> articles;
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("message")
    private String message;
}

