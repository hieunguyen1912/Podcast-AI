package com.hieunguyen.podcastai.service.impl;

import com.hieunguyen.podcastai.dto.request.TagRequest;
import com.hieunguyen.podcastai.dto.request.TagUpdateRequest;
import com.hieunguyen.podcastai.dto.response.TagDto;
import com.hieunguyen.podcastai.entity.Tag;
import com.hieunguyen.podcastai.enums.ErrorCode;
import com.hieunguyen.podcastai.exception.AppException;
import com.hieunguyen.podcastai.mapper.TagMapper;
import com.hieunguyen.podcastai.repository.TagRepository;
import com.hieunguyen.podcastai.service.TagService;
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
public class TagServiceImpl implements TagService {
    
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    
    @Override
    public TagDto createTag(TagRequest request) {
        log.info("Creating tag with name: {}", request.getName());
        
        // Validation is handled by @UniqueTagName validator in TagRequest
        Tag tag = tagMapper.toEntity(request);
        tag.setUsageCount(0);
        tag.setIsTrending(false);
        
        Tag savedTag = tagRepository.save(tag);
        log.info("Successfully created tag with ID: {}", savedTag.getId());
        
        return tagMapper.toDto(savedTag);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TagDto getTagById(Long id) {
        log.info("Getting tag by ID: {}", id);
        
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
        
        return tagMapper.toDto(tag);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TagDto getTagByName(String name) {
        log.info("Getting tag by name: {}", name);
        
        Tag tag = tagRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new AppException(ErrorCode.TAG_NAME_NOT_FOUND));
        
        return tagMapper.toDto(tag);
    }
    
    @Override
    public TagDto updateTag(Long id, TagUpdateRequest request) {
        log.info("Updating tag with ID: {}", id);
        
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
        
        // Check if name is being changed and if new name already exists
        if (request.getName() != null && !request.getName().equals(tag.getName())) {
            if (tagRepository.existsByNameIgnoreCase(request.getName())) {
                throw new AppException(ErrorCode.TAG_NAME_EXISTS);
            }
        }
        
        tagMapper.updateEntity(request, tag);
        Tag updatedTag = tagRepository.save(tag);
        
        log.info("Successfully updated tag with ID: {}", updatedTag.getId());
        return tagMapper.toDto(updatedTag);
    }
    
    @Override
    public void deleteTag(Long id) {
        log.info("Deleting tag with ID: {}", id);
        
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
        
        tagRepository.delete(tag);
        log.info("Successfully deleted tag with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TagDto> getAllTags(Pageable pageable) {
        log.info("Getting all tags with pagination: {}", pageable);
        
        Page<Tag> tags = tagRepository.findAll(pageable);
        return tags.map(tagMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        log.info("Getting all tags");
        
        List<Tag> tags = tagRepository.findAll();
        return tagMapper.toDtoList(tags);
    }
    
    
    @Override
    public TagDto incrementUsageCount(Long id) {
        log.info("Incrementing usage count for tag with ID: {}", id);
        
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
        
        tag.setUsageCount(tag.getUsageCount() + 1);
        Tag updatedTag = tagRepository.save(tag);
        
        log.info("Successfully incremented usage count for tag with ID: {}", id);
        return tagMapper.toDto(updatedTag);
    }
    
    @Override
    public TagDto decrementUsageCount(Long id) {
        log.info("Decrementing usage count for tag with ID: {}", id);
        
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
        
        if (tag.getUsageCount() > 0) {
            tag.setUsageCount(tag.getUsageCount() - 1);
        }
        
        Tag updatedTag = tagRepository.save(tag);
        
        log.info("Successfully decremented usage count for tag with ID: {}", id);
        return tagMapper.toDto(updatedTag);
    }
    
    @Override
    public TagDto setTrendingStatus(Long id, Boolean isTrending) {
        log.info("Setting trending status for tag with ID: {} to {}", id, isTrending);
        
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TAG_NOT_FOUND));
        
        tag.setIsTrending(isTrending);
        Tag updatedTag = tagRepository.save(tag);
        
        log.info("Successfully set trending status for tag with ID: {}", id);
        return tagMapper.toDto(updatedTag);
    }
}
