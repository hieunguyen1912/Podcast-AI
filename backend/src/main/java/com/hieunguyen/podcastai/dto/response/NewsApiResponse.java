package com.hieunguyen.podcastai.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsApiResponse {

    @JsonProperty("status")
    private String status; // "ok" or "error"

    @JsonProperty("totalResults")
    private Integer totalResults; // Total number of articles

    @JsonProperty("articles")
    private List<NewsArticle> articles; // List of articles

    @JsonProperty("message")
    private String message; // Error message if status is "error"

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NewsArticle {

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
        private String publishedAt;

        @JsonProperty("content")
        private String content;
    }

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