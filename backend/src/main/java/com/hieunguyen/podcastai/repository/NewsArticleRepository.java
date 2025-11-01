package com.hieunguyen.podcastai.repository;

import com.hieunguyen.podcastai.entity.NewsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long>, JpaSpecificationExecutor<NewsArticle> {
    
    Optional<NewsArticle> findByUrl(String url);

    @Query(
            value = "SELECT n.* FROM news_articles n " +
                    "WHERE to_tsvector('english', " +
                    "  COALESCE(n.title, '') || ' ' || " +
                    "  COALESCE(n.description, '') || ' ' || " +
                    "  COALESCE(n.content, '')) " +
                    "@@ plainto_tsquery('english', :keyword) " +
                    "AND (CAST(:categoryId AS BIGINT) IS NULL OR n.category_id = :categoryId) " +
                    "AND (CAST(:fromDate AS TIMESTAMP) IS NULL OR n.published_at >= :fromDate) " +
                    "AND (CAST(:toDate AS TIMESTAMP) IS NULL OR n.published_at <= :toDate) " +
                    "ORDER BY ts_rank(" +
                    "  to_tsvector('english', " +
                    "    COALESCE(n.title, '') || ' ' || " +
                    "    COALESCE(n.description, '') || ' ' || " +
                    "    COALESCE(n.content, '')), " +
                    "  plainto_tsquery('english', :keyword)" +
                    ") DESC, " +
                    "n.published_at DESC",
            nativeQuery = true
    )
    Page<NewsArticle> fullTextSearch(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate,
            Pageable pageable);

    Page<NewsArticle> findByCategoryId(Long categoryId, Pageable pageable);
    
}