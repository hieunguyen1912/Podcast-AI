package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.dto.request.CategoryRequest;
import com.hieunguyen.podcastai.dto.request.CategoryUpdateRequest;
import com.hieunguyen.podcastai.dto.response.CategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryRequest request);

    CategoryDto getCategoryById(Long id);

    CategoryDto getCategoryByName(String name);

    CategoryDto getCategoryBySlug(String slug);

    CategoryDto updateCategory(Long id, CategoryUpdateRequest request);

    void deleteCategory(Long id);

    Page<CategoryDto> getAllCategories(Pageable pageable);

    List<CategoryDto> getAllCategories();
}
