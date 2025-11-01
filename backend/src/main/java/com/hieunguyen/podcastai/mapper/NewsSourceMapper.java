package com.hieunguyen.podcastai.mapper;

import com.hieunguyen.podcastai.dto.request.NewsSourceRequest;
import com.hieunguyen.podcastai.dto.response.NewsSourceDto;
import com.hieunguyen.podcastai.entity.NewsSource;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NewsSourceMapper {
    NewsSource toEntity(NewsSourceRequest newsSourceRequest);
    NewsSourceDto toNewsSourceDto(NewsSource newsSource);

    void update(@MappingTarget NewsSource newsSource, NewsSourceRequest request);
}
