package com.hieunguyen.podcastai.repository;

import com.hieunguyen.podcastai.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    /**
     * Find tag by name (case-insensitive)
     */
    Optional<Tag> findByNameIgnoreCase(String name);
    
    /**
     * Check if tag exists by name (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
}
