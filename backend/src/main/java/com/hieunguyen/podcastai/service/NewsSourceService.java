package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.dto.request.NewsSourceRequest;
import com.hieunguyen.podcastai.dto.response.NewsSourceDto;

import java.util.List;

public interface NewsSourceService {
    List<NewsSourceDto> getAllSources();
    NewsSourceDto getSourceById(Long id);
    NewsSourceDto updateSource(Long id, NewsSourceRequest request);
    void unActivateSource(Long id);
    void activateSource(Long id);
}
