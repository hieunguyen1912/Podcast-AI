package com.hieunguyen.podcastai.mapper;

import com.hieunguyen.podcastai.dto.request.CategoryRequest;
import com.hieunguyen.podcastai.dto.request.CategoryUpdateRequest;
import com.hieunguyen.podcastai.dto.response.CategoryDto;
import com.hieunguyen.podcastai.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    Category toEntity(CategoryRequest request);

    CategoryDto toDto(Category category);

    List<CategoryDto> toDtoList(List<Category> categories);

    void updateEntity(CategoryUpdateRequest request, @MappingTarget Category category);

    Category toEntityForUpdate(CategoryRequest request);

    @Named("mapSubCategories")
    default List<CategoryDto> mapSubCategories(List<Category> subCategories) {
        if (subCategories == null || subCategories.isEmpty()) {
            return null;
        }
        return toDtoList(subCategories);
    }
}