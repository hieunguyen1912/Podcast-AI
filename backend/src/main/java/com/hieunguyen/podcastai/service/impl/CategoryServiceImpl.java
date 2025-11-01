package com.hieunguyen.podcastai.service.impl;

import com.hieunguyen.podcastai.dto.request.CategoryRequest;
import com.hieunguyen.podcastai.dto.request.CategoryUpdateRequest;
import com.hieunguyen.podcastai.dto.response.CategoryDto;
import com.hieunguyen.podcastai.entity.Category;
import com.hieunguyen.podcastai.enums.ErrorCode;
import com.hieunguyen.podcastai.exception.AppException;
import com.hieunguyen.podcastai.mapper.CategoryMapper;
import com.hieunguyen.podcastai.repository.CategoryRepository;
import com.hieunguyen.podcastai.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    @Override
    public CategoryDto createCategory(CategoryRequest request) {
        log.info("Creating category with name: {}", request.getName());
        
        // Validation is handled by @UniqueCategoryName and @UniqueCategorySlug validators in CategoryRequest
        Category category = categoryMapper.toEntity(request);
        
        Category savedCategory = categoryRepository.save(category);
        log.info("Successfully created category with ID: {}", savedCategory.getId());
        
        return categoryMapper.toDto(savedCategory);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        log.info("Getting category by ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        
        return categoryMapper.toDto(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryByName(String name) {
        log.info("Getting category by name: {}", name);
        
        Category category = categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NAME_NOT_FOUND));
        
        return categoryMapper.toDto(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryBySlug(String slug) {
        log.info("Getting category by slug: {}", slug);
        
        Category category = categoryRepository.findBySlugIgnoreCase(slug)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_SLUG_NOT_FOUND));
        
        return categoryMapper.toDto(category);
    }
    
    @Override
    public CategoryDto updateCategory(Long id, CategoryUpdateRequest request) {
        log.info("Updating category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        
        // Check if name is being changed and if new name already exists
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
                throw new AppException(ErrorCode.CATEGORY_NAME_EXISTS);
            }
        }
        
        // Check if slug is being changed and if new slug already exists
        if (request.getSlug() != null && !request.getSlug().equals(category.getSlug())) {
            if (categoryRepository.existsBySlugIgnoreCase(request.getSlug())) {
                throw new AppException(ErrorCode.CATEGORY_SLUG_EXISTS);
            }
        }

        categoryMapper.updateEntity(request, category);
        Category updatedCategory = categoryRepository.save(category);
        
        log.info("Successfully updated category with ID: {}", updatedCategory.getId());
        return categoryMapper.toDto(updatedCategory);
    }
    
    @Override
    public void deleteCategory(Long id) {
        log.info("Deleting category with ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        categoryRepository.delete(category);
        log.info("Successfully deleted category with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDto> getAllCategories(Pageable pageable) {
        log.info("Getting all categories with pagination: {}", pageable);
        
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(categoryMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        log.info("Getting all categories");
        
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toDtoList(categories);
    }
}
