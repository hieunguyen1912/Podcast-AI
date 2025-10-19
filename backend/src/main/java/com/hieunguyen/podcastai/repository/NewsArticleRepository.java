package com.hieunguyen.podcastai.repository;

import com.hieunguyen.podcastai.entity.NewsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    
    Optional<NewsArticle> findByUrl(String url);
    
    // Search methods
    Page<NewsArticle> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String titleKeyword, String descriptionKeyword, Pageable pageable);
    
    // Category methods
    Page<NewsArticle> findByCategoryId(Long categoryId, Pageable pageable);
    
    // Source methods
    Page<NewsArticle> findByNewsSourceId(Long sourceId, Pageable pageable);
    
    // Language methods
    Page<NewsArticle> findByLanguage(String language, Pageable pageable);
    
    // Related articles methods
    Page<NewsArticle> findByCategoryIdAndIdNot(Long categoryId, Long excludeId, Pageable pageable);
    
    Page<NewsArticle> findByIdNot(Long excludeId, Pageable pageable);
    
    // Statistics methods
    @Query("SELECT COUNT(a) FROM NewsArticle a")
    Long countAllArticles();
    
    @Query("SELECT SUM(a.viewCount) FROM NewsArticle a")
    Long sumAllViewCounts();
    
    // Find by multiple criteria
    @Query("SELECT a FROM NewsArticle a WHERE " +
           "(:categoryId IS NULL OR a.category.id = :categoryId) AND " +
           "(:sourceId IS NULL OR a.newsSource.id = :sourceId) AND " +
           "(:language IS NULL OR a.language = :language) AND " +
           "(:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<NewsArticle> findByMultipleCriteria(
        @Param("categoryId") Long categoryId,
        @Param("sourceId") Long sourceId,
        @Param("language") String language,
        @Param("keyword") String keyword,
        Pageable pageable);
}