package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.entity.NewsArticle;
import com.hieunguyen.podcastai.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {

    private final NewsArticleRepository newsArticleRepository;

    /**
     * Get all news articles with pagination and sorting
     */
    public Page<NewsArticle> getAllNews(int page, int size, String sortBy, String sortDir) {
        log.info("Getting all news articles - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return newsArticleRepository.findAll(pageable);
    }

    /**
     * Get news article by ID
     */
    @Transactional
    public Optional<NewsArticle> getNewsById(Long id) {
        log.info("Getting news article by ID: {}", id);
        
        Optional<NewsArticle> articleOpt = newsArticleRepository.findById(id);
        
        if (articleOpt.isPresent()) {
            NewsArticle article = articleOpt.get();
            // Increment view count
            article.setViewCount(article.getViewCount() + 1);
            newsArticleRepository.save(article);
            log.debug("Incremented view count for article: {}", article.getTitle());
        }
        
        return articleOpt;
    }

    /**
     * Search news articles by keyword
     */
    public Page<NewsArticle> searchNews(String keyword, int page, int size) {
        log.info("Searching news articles with keyword: {} - page: {}, size: {}", keyword, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        
        return newsArticleRepository
            .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword, keyword, pageable);
    }

    /**
     * Get trending news (most viewed)
     */
    public List<NewsArticle> getTrendingNews(int limit) {
        log.info("Getting trending news - limit: {}", limit);
        
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "viewCount"));
        Page<NewsArticle> articlesPage = newsArticleRepository.findAll(pageable);
        
        return articlesPage.getContent();
    }

    /**
     * Get latest news
     */
    public List<NewsArticle> getLatestNews(int limit) {
        log.info("Getting latest news - limit: {}", limit);
        
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<NewsArticle> articlesPage = newsArticleRepository.findAll(pageable);
        
        return articlesPage.getContent();
    }

    /**
     * Get news by category
     */
    public Page<NewsArticle> getNewsByCategory(Long categoryId, int page, int size) {
        log.info("Getting news by category ID: {} - page: {}, size: {}", categoryId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        
        return newsArticleRepository.findByCategoryId(categoryId, pageable);
    }

    /**
     * Get news by source
     */
    public Page<NewsArticle> getNewsBySource(Long sourceId, int page, int size) {
        log.info("Getting news by source ID: {} - page: {}, size: {}", sourceId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        
        return newsArticleRepository.findByNewsSourceId(sourceId, pageable);
    }

    /**
     * Get news by language
     */
    public Page<NewsArticle> getNewsByLanguage(String language, int page, int size) {
        log.info("Getting news by language: {} - page: {}, size: {}", language, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        
        return newsArticleRepository.findByLanguage(language, pageable);
    }

    /**
     * Get news statistics
     */
    public NewsStatistics getNewsStatistics() {
        log.info("Getting news statistics");
        
        long totalArticles = newsArticleRepository.count();
        long totalViews = newsArticleRepository.findAll().stream()
            .mapToLong(NewsArticle::getViewCount)
            .sum();
        
        // Get most viewed article
        Optional<NewsArticle> mostViewed = newsArticleRepository.findAll().stream()
            .max((a1, a2) -> Long.compare(a1.getViewCount(), a2.getViewCount()));
        
        return NewsStatistics.builder()
            .totalArticles(totalArticles)
            .totalViews(totalViews)
            .mostViewedArticle(mostViewed.orElse(null))
            .build();
    }

    /**
     * Like an article
     */
    @Transactional
    public boolean likeArticle(Long articleId) {
        log.info("Liking article: {}", articleId);
        
        Optional<NewsArticle> articleOpt = newsArticleRepository.findById(articleId);
        if (articleOpt.isPresent()) {
            NewsArticle article = articleOpt.get();
            article.setLikeCount(article.getLikeCount() + 1);
            newsArticleRepository.save(article);
            log.debug("Liked article: {}", article.getTitle());
            return true;
        }
        
        return false;
    }

    /**
     * Share an article
     */
    @Transactional
    public boolean shareArticle(Long articleId) {
        log.info("Sharing article: {}", articleId);
        
        Optional<NewsArticle> articleOpt = newsArticleRepository.findById(articleId);
        if (articleOpt.isPresent()) {
            NewsArticle article = articleOpt.get();
            article.setShareCount(article.getShareCount() + 1);
            newsArticleRepository.save(article);
            log.debug("Shared article: {}", article.getTitle());
            return true;
        }
        
        return false;
    }

    /**
     * Get related articles (same category or similar tags)
     */
    public List<NewsArticle> getRelatedArticles(Long articleId, int limit) {
        log.info("Getting related articles for: {} - limit: {}", articleId, limit);
        
        Optional<NewsArticle> articleOpt = newsArticleRepository.findById(articleId);
        if (articleOpt.isEmpty()) {
            return List.of();
        }
        
        NewsArticle article = articleOpt.get();
        
        // Get articles from same category, excluding current article
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "publishedAt"));
        
        if (article.getCategory() != null) {
            Page<NewsArticle> relatedPage = newsArticleRepository
                .findByCategoryIdAndIdNot(article.getCategory().getId(), articleId, pageable);
            return relatedPage.getContent();
        }
        
        // Fallback: get latest articles
        Page<NewsArticle> latestPage = newsArticleRepository
            .findByIdNot(articleId, pageable);
        return latestPage.getContent();
    }

    /**
     * News statistics DTO
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NewsStatistics {
        private Long totalArticles;
        private Long totalViews;
        private NewsArticle mostViewedArticle;
    }
}
