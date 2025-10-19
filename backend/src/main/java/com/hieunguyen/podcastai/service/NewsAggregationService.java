package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.entity.NewsArticle;
import com.hieunguyen.podcastai.entity.NewsSource;
import com.hieunguyen.podcastai.enums.NewsSourceType;

import java.util.List;

public interface NewsAggregationService {

    /**
     * Fetch news from all active sources
     * @return Number of articles fetched
     */
    int fetchAllNews();

    /**
     * Fetch news from specific source
     * @param sourceId Source ID
     * @return Number of articles fetched
     */
    int fetchNewsFromSource(Long sourceId);

    /**
     * Fetch news from sources by type
     * @param sourceType Source type
     * @return Number of articles fetched
     */
    int fetchNewsFromSourceType(NewsSourceType sourceType);

    /**
     * Process and save articles
     * @param articles List of articles
     * @param source Source information
     * @return Number of articles saved
     */
    int processAndSaveArticles(List<NewsArticle> articles, NewsSource source);

    /**
     * Update source status after fetch
     * @param source Source
     * @param success Whether fetch was successful
     * @param articleCount Number of articles fetched
     */
    void updateSourceStatus(NewsSource source, boolean success, int articleCount);

    /**
     * Health check for all sources
     * @return Health status
     */
    boolean checkAllSourcesHealth();
}
