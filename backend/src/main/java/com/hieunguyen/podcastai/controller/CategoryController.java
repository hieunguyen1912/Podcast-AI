package com.hieunguyen.podcastai.controller;

import com.hieunguyen.podcastai.dto.request.CategoryRequest;
import com.hieunguyen.podcastai.dto.request.CategoryUpdateRequest;
import com.hieunguyen.podcastai.dto.response.ApiResponse;
import com.hieunguyen.podcastai.dto.response.CategoryDto;
import com.hieunguyen.podcastai.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Slf4j
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    /**
     * Create a new category
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("Creating category with name: {}", request.getName());
        
        CategoryDto category = categoryService.createCategory(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Category created successfully", category));
    }
    
    /**
     * Get category by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryById(@PathVariable Long id) {
        log.info("Getting category by ID: {}", id);
        
        CategoryDto category = categoryService.getCategoryById(id);
        
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }
    
    /**
     * Get category by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryByName(@PathVariable String name) {
        log.info("Getting category by name: {}", name);
        
        CategoryDto category = categoryService.getCategoryByName(name);
        
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }
    
    /**
     * Get category by slug
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryBySlug(@PathVariable String slug) {
        log.info("Getting category by slug: {}", slug);
        
        CategoryDto category = categoryService.getCategoryBySlug(slug);
        
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }
    
    /**
     * Update category by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@PathVariable Long id, 
                                                                  @Valid @RequestBody CategoryUpdateRequest request) {
        log.info("Updating category with ID: {}", id);
        
        CategoryDto category = categoryService.updateCategory(id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }
    
    /**
     * Delete category by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        log.info("Deleting category with ID: {}", id);
        
        categoryService.deleteCategory(id);
        
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
    
    /**
     * Get all categories with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryDto>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sortOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        log.info("Getting all categories with pagination: page={}, size={}, sortBy={}, sortDirection={}", 
                page, size, sortBy, sortDirection);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CategoryDto> categories = categoryService.getAllCategories(pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }
    
    /**
     * Get all categories (no pagination)
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        log.info("Getting all categories");
        
        List<CategoryDto> categories = categoryService.getAllCategories();
        
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }
    
    
    /**
     * Get root categories (no parent)
     */
    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getRootCategories() {
        log.info("Getting root categories");
        
        List<CategoryDto> categories = categoryService.getRootCategories();
        
        return ResponseEntity.ok(ApiResponse.success("Root categories retrieved successfully", categories));
    }
    
    
    /**
     * Get subcategories by parent category ID
     */
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getSubcategories(@PathVariable Long parentId) {
        log.info("Getting subcategories for parent ID: {}", parentId);
        
        List<CategoryDto> categories = categoryService.getSubcategories(parentId);
        
        return ResponseEntity.ok(ApiResponse.success("Subcategories retrieved successfully", categories));
    }
    
    
    /**
     * Check if category exists by name
     */
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<ApiResponse<Boolean>> existsByName(@PathVariable String name) {
        log.info("Checking if category exists by name: {}", name);
        
        boolean exists = categoryService.existsByName(name);
        
        return ResponseEntity.ok(ApiResponse.success("Category existence checked successfully", exists));
    }
    
    /**
     * Check if category exists by slug
     */
    @GetMapping("/exists/slug/{slug}")
    public ResponseEntity<ApiResponse<Boolean>> existsBySlug(@PathVariable String slug) {
        log.info("Checking if category exists by slug: {}", slug);
        
        boolean exists = categoryService.existsBySlug(slug);
        
        return ResponseEntity.ok(ApiResponse.success("Category existence checked successfully", exists));
    }
    
}
