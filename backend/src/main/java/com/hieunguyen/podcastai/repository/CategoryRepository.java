package com.hieunguyen.podcastai.repository;

import com.hieunguyen.podcastai.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Find category by name (case-insensitive)
     */
    Optional<Category> findByNameIgnoreCase(String name);
    
    /**
     * Find category by slug (case-insensitive)
     */
    Optional<Category> findBySlugIgnoreCase(String slug);
    
    /**
     * Check if category exists by name (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Check if category exists by slug (case-insensitive)
     */
    boolean existsBySlugIgnoreCase(String slug);
    
    /**
     * Find root categories (no parent)
     */
    List<Category> findByParentCategoryIsNull();
}