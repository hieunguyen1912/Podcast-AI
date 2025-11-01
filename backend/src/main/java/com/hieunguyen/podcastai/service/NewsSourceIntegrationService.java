package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.entity.NewsArticle;
import com.hieunguyen.podcastai.entity.NewsSource;

import java.util.List;

public interface NewsSourceIntegrationService {

    List<NewsArticle> fetchNews(NewsSource source);

    boolean isHealthy();

    String getSourceType();
}