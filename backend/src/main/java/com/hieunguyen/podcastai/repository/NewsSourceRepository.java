package com.hieunguyen.podcastai.repository;

import com.hieunguyen.podcastai.entity.NewsSource;
import com.hieunguyen.podcastai.enums.NewsSourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
        
import java.util.List;

@Repository
public interface NewsSourceRepository extends JpaRepository<NewsSource, Long> {
    List<NewsSource> findByIsActiveTrue();

    List<NewsSource> findByTypeAndIsActiveTrue(NewsSourceType type);

    @Modifying
    @Query("UPDATE NewsSource n SET n.isActive = :active WHERE n.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);
}
