package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.dto.request.CategoryRequest;
import com.hieunguyen.podcastai.dto.request.CategoryUpdateRequest;
import com.hieunguyen.podcastai.dto.response.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    
    /**
     * Create a new category
     */
    CategoryDto createCategory(CategoryRequest request);
    
    /**
     * Get category by ID
     */
    CategoryDto getCategoryById(Long id);
    
    /**
     * Get category by name
     */
    CategoryDto getCategoryByName(String name);
    
    /**
     * Get category by slug
     */
    CategoryDto getCategoryBySlug(String slug);
    
    /**
     * Update category by ID
     */
    CategoryDto updateCategory(Long id, CategoryUpdateRequest request);
    
    /**
     * Delete category by ID
     */
    void deleteCategory(Long id);
    
    /**
     * Get all categories with pagination
     */
    Page<CategoryDto> getAllCategories(Pageable pageable);
    
    /**
     * Get all categories
     */
    List<CategoryDto> getAllCategories();
    
    /**
     * Get root categories (no parent)
     */
    List<CategoryDto> getRootCategories();
    
    /**
     * Get subcategories by parent category ID
     */
    List<CategoryDto> getSubcategories(Long parentId);
    
    /**
     * Check if category exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Check if category exists by slug
     */
    boolean existsBySlug(String slug);
}
