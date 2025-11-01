package com.hieunguyen.podcastai.mapper;

import java.util.List;

import com.hieunguyen.podcastai.entity.Category;
import com.hieunguyen.podcastai.entity.NewsSource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.hieunguyen.podcastai.dto.request.FetchConfigurationRequest;
import com.hieunguyen.podcastai.dto.response.FetchConfigurationDto;
import com.hieunguyen.podcastai.entity.FetchConfiguration;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FetchConfigurationMapper {

    @Mapping(source = "categoryId", target = "category")
    @Mapping(source = "newsSourceId", target = "newsSource")
    FetchConfiguration toEntity(FetchConfigurationRequest request);

    default Category mapCategory(Long categoryId) {
        if (categoryId == null) return null;
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }

    default NewsSource mapNewsSource(Long sourceId) {
        if (sourceId == null) return null;
        NewsSource newsSource = new NewsSource();
        newsSource.setId(sourceId);
        return newsSource;
    }

    @Mapping(target = "sourceName", source = "newsSource.name")
    @Mapping(target = "categoryName", source = "category.name")
    FetchConfigurationDto toDto(FetchConfiguration fetchConfiguration);

    List<FetchConfigurationDto> toDtoList(List<FetchConfiguration> fetchConfigurations);
    void updateEntity(FetchConfigurationRequest request, @MappingTarget FetchConfiguration fetchConfiguration);
    FetchConfiguration toEntityForUpdate(FetchConfigurationRequest request);
}