package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.entity.NewsArticle;
import com.hieunguyen.podcastai.entity.NewsSource;

import java.util.List;

public interface NewsSourceIntegrationService {
    
    /**
     * Fetch news from the source
     * @param source NewsSource entity
     * @return List of NewsArticle entities
     */
    List<NewsArticle> fetchNews(NewsSource source);
    
    /**
     * Check if the source is healthy
     * @return boolean
     */
    boolean isHealthy();
    
    /**
     * Get source type
     * @return String
     */
    String getSourceType();
}