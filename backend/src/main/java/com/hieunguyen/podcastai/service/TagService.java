package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.dto.request.TagRequest;
import com.hieunguyen.podcastai.dto.request.TagUpdateRequest;
import com.hieunguyen.podcastai.dto.response.TagDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    
    /**
     * Create a new tag
     */
    TagDto createTag(TagRequest request);
    
    /**
     * Get tag by ID
     */
    TagDto getTagById(Long id);
    
    /**
     * Get tag by name
     */
    TagDto getTagByName(String name);
    
    /**
     * Update tag by ID
     */
    TagDto updateTag(Long id, TagUpdateRequest request);
    
    /**
     * Delete tag by ID
     */
    void deleteTag(Long id);
    
    /**
     * Get all tags with pagination
     */
    Page<TagDto> getAllTags(Pageable pageable);
    
    /**
     * Get all tags
     */
    List<TagDto> getAllTags();
    
    /**
     * Increment usage count for tag
     */
    TagDto incrementUsageCount(Long id);
    
    /**
     * Decrement usage count for tag
     */
    TagDto decrementUsageCount(Long id);
    
    /**
     * Set trending status for tag
     */
    TagDto setTrendingStatus(Long id, Boolean isTrending);
}
