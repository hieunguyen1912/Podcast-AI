package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.dto.NewsArticle;

import java.util.List;


public interface NewsContentService {
    
    List<NewsArticle> searchNews(String query);
    
    List<NewsArticle> searchNews(String query, String language, String sortBy, int pageSize);
    
    String getArticleContent(String articleUrl);
    
    List<NewsArticle> getTrendingNews(String category);
    
    List<NewsArticle> getTopHeadlines(String country, String category, int pageSize);
   
    String processNewsContent(List<NewsArticle> articles, int maxLength);
    
    List<NewsArticle> filterAndRankArticles(List<NewsArticle> articles, int maxArticles);
}

