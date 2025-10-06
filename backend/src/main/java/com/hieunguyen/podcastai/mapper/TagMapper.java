package com.hieunguyen.podcastai.mapper;

import com.hieunguyen.podcastai.dto.request.TagRequest;
import com.hieunguyen.podcastai.dto.request.TagUpdateRequest;
import com.hieunguyen.podcastai.dto.response.TagDto;
import com.hieunguyen.podcastai.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TagMapper {
    
    /**
     * Convert TagRequest to Tag entity
     */ 
    @Mapping(target = "usageCount", ignore = true)
    Tag toEntity(TagRequest request);
    
    /**
     * Convert Tag entity to TagDto
     */
    TagDto toDto(Tag tag);
    
    /**
     * Convert list of Tag entities to list of TagDto
     */
    List<TagDto> toDtoList(List<Tag> tags);
    
    /**
     * Update Tag entity from TagUpdateRequest
     */
    @Mapping(target = "usageCount", ignore = true)
    void updateEntity(TagUpdateRequest request, @MappingTarget Tag tag);
    
    /**
     * Convert TagRequest to Tag entity for update operations
     */
    @Mapping(target = "usageCount", ignore = true)
    Tag toEntityForUpdate(TagRequest request);
}
