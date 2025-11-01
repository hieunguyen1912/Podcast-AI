package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.entity.NewsArticle;
import com.hieunguyen.podcastai.entity.NewsSource;
import com.hieunguyen.podcastai.enums.NewsSourceType;

import java.util.List;

public interface NewsAggregationService {
    int fetchAllNews();

    int fetchNewsFromSource(Long sourceId);

    int fetchNewsFromSourceType(NewsSourceType sourceType);

    int processAndSaveArticles(List<NewsArticle> articles, NewsSource source);

    void updateSourceStatus(NewsSource source, boolean success, int articleCount);

    boolean checkAllSourcesHealth();
}
