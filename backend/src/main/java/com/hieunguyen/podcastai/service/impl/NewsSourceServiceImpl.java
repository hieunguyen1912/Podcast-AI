package com.hieunguyen.podcastai.service.impl;

import com.hieunguyen.podcastai.dto.request.NewsSourceRequest;
import com.hieunguyen.podcastai.dto.response.NewsSourceDto;
import com.hieunguyen.podcastai.entity.NewsSource;
import com.hieunguyen.podcastai.enums.ErrorCode;
import com.hieunguyen.podcastai.exception.AppException;
import com.hieunguyen.podcastai.mapper.NewsSourceMapper;
import com.hieunguyen.podcastai.repository.NewsSourceRepository;
import com.hieunguyen.podcastai.service.NewsSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsSourceServiceImpl implements NewsSourceService {

    private final NewsSourceRepository newsSourceRepository;
    private final NewsSourceMapper newsSourceMapper;

    @Override
    public List<NewsSourceDto> getAllSources() {
        List<NewsSource> newsSource = newsSourceRepository.findAll();

        return newsSource.stream().map(newsSourceMapper::toNewsSourceDto).toList();
    }

    @Override
    public NewsSourceDto getSourceById(Long id) {
        NewsSource newsSource = newsSourceRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND)
        );
        return newsSourceMapper.toNewsSourceDto(newsSource);
    }

    @Override
    @Transactional
    public NewsSourceDto updateSource(Long id, NewsSourceRequest request) {
        NewsSource newsSource = newsSourceRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND)
        );

        newsSourceMapper.update(newsSource, request);


        return newsSourceMapper.toNewsSourceDto(newsSource);
    }

    @Override
    @Transactional
    public void unActivateSource(Long id) {
        newsSourceRepository.updateActiveStatus(id, false);
    }

    @Override
    @Transactional
    public void activateSource(Long id) {
        newsSourceRepository.updateActiveStatus(id, true);
    }
}
