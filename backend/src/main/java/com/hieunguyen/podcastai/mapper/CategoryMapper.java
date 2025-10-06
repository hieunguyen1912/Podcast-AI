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
    
    /**
     * Convert CategoryRequest to Category entity
     */
   
    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    Category toEntity(CategoryRequest request);
    
    /**
     * Convert Category entity to CategoryDto
     */
    @Mapping(target = "parentCategoryId", source = "parentCategory.id")
    @Mapping(target = "parentCategoryName", source = "parentCategory.name")
    @Mapping(target = "subCategories", source = "subCategories", qualifiedByName = "mapSubCategories")
    CategoryDto toDto(Category category);
    
    /**
     * Convert list of Category entities to list of CategoryDto
     */
    List<CategoryDto> toDtoList(List<Category> categories);
    
    /**
     * Update Category entity from CategoryUpdateRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(CategoryUpdateRequest request, @MappingTarget Category category);
    
    /**
     * Convert CategoryRequest to Category entity for update operations
     */
    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    Category toEntityForUpdate(CategoryRequest request);
    
    /**
     * Map subcategories recursively
     */
    @Named("mapSubCategories")
    default List<CategoryDto> mapSubCategories(List<Category> subCategories) {
        if (subCategories == null || subCategories.isEmpty()) {
            return null;
        }
        return toDtoList(subCategories);
    }
}