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

    Tag toEntity(TagRequest request);

    TagDto toDto(Tag tag);

    List<TagDto> toDtoList(List<Tag> tags);

    void updateEntity(TagUpdateRequest request, @MappingTarget Tag tag);

    Tag toEntityForUpdate(TagRequest request);
}
