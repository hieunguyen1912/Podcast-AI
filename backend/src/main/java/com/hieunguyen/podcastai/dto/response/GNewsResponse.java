package com.hieunguyen.podcastai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GNewsResponse {
    private int totalArticles;
    private List<Article> article;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Article {
        private String id;
        private String title;
        private String description;
        private String content;
        private String url;
        private String image;
        private String publishedAt;
        private String lang;
        private Source source;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Source {
        private String id;
        private String name;
        private String url;
        private String country;
    }



}
