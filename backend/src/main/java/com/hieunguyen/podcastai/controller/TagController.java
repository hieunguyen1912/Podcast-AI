package com.hieunguyen.podcastai.controller;

import com.hieunguyen.podcastai.dto.request.TagRequest;
import com.hieunguyen.podcastai.dto.request.TagUpdateRequest;
import com.hieunguyen.podcastai.dto.response.ApiResponse;
import com.hieunguyen.podcastai.dto.response.TagDto;
import com.hieunguyen.podcastai.service.TagService;
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
@RequestMapping("/api/v1/tags")
@Slf4j
@RequiredArgsConstructor
public class TagController {
    
    private final TagService tagService;
    
    /**
     * Create a new tag
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TagDto>> createTag(@Valid @RequestBody TagRequest request) {
        log.info("Creating tag with name: {}", request.getName());
        
        TagDto tag = tagService.createTag(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Tag created successfully", tag));
    }
    
    /**
     * Get tag by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TagDto>> getTagById(@PathVariable Long id) {
        log.info("Getting tag by ID: {}", id);
        
        TagDto tag = tagService.getTagById(id);
        
        return ResponseEntity.ok(ApiResponse.success("Tag retrieved successfully", tag));
    }
    
    /**
     * Get tag by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<TagDto>> getTagByName(@PathVariable String name) {
        log.info("Getting tag by name: {}", name);
        
        TagDto tag = tagService.getTagByName(name);
        
        return ResponseEntity.ok(ApiResponse.success("Tag retrieved successfully", tag));
    }
    
    /**
     * Update tag by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TagDto>> updateTag(@PathVariable Long id, 
                                                       @Valid @RequestBody TagUpdateRequest request) {
        log.info("Updating tag with ID: {}", id);
        
        TagDto tag = tagService.updateTag(id, request);
        
        return ResponseEntity.ok(ApiResponse.success("Tag updated successfully", tag));
    }
    
    /**
     * Delete tag by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTag(@PathVariable Long id) {
        log.info("Deleting tag with ID: {}", id);
        
        tagService.deleteTag(id);
        
        return ResponseEntity.ok(ApiResponse.success("Tag deleted successfully", null));
    }
    
    /**
     * Get all tags with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TagDto>>> getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        log.info("Getting all tags with pagination: page={}, size={}, sortBy={}, sortDirection={}", 
                page, size, sortBy, sortDirection);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TagDto> tags = tagService.getAllTags(pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Tags retrieved successfully", tags));
    }
    
    /**
     * Get all tags (no pagination)
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TagDto>>> getAllTags() {
        log.info("Getting all tags");
        
        List<TagDto> tags = tagService.getAllTags();
        
        return ResponseEntity.ok(ApiResponse.success("Tags retrieved successfully", tags));
    }
    
    
    /**
     * Increment usage count for tag
     */
    @PostMapping("/{id}/increment-usage")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TagDto>> incrementUsageCount(@PathVariable Long id) {
        log.info("Incrementing usage count for tag with ID: {}", id);
        
        TagDto tag = tagService.incrementUsageCount(id);
        
        return ResponseEntity.ok(ApiResponse.success("Usage count incremented successfully", tag));
    }
    
    /**
     * Decrement usage count for tag
     */
    @PostMapping("/{id}/decrement-usage")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<TagDto>> decrementUsageCount(@PathVariable Long id) {
        log.info("Decrementing usage count for tag with ID: {}", id);
        
        TagDto tag = tagService.decrementUsageCount(id);
        
        return ResponseEntity.ok(ApiResponse.success("Usage count decremented successfully", tag));
    }
    
    /**
     * Set trending status for tag
     */
    @PutMapping("/{id}/trending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TagDto>> setTrendingStatus(@PathVariable Long id, 
                                                               @RequestParam Boolean isTrending) {
        log.info("Setting trending status for tag with ID: {} to {}", id, isTrending);
        
        TagDto tag = tagService.setTrendingStatus(id, isTrending);
        
        return ResponseEntity.ok(ApiResponse.success("Trending status updated successfully", tag));
    }

}
