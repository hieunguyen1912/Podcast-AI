package com.hieunguyen.podcastai.repository;

import com.hieunguyen.podcastai.entity.NewsSource;
import com.hieunguyen.podcastai.enums.NewsSourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface NewsSourceRepository extends JpaRepository<NewsSource, Long> {

    /**
     * Find all active sources
     */
    List<NewsSource> findByIsActiveTrueOrderByPriorityDesc();

    /**
     * Find sources by type
     */
    List<NewsSource> findByTypeAndIsActiveTrueOrderByPriorityDesc(NewsSourceType type);
}
