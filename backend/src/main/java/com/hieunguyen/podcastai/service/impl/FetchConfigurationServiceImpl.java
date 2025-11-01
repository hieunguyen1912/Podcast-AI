package com.hieunguyen.podcastai.service.impl;

import java.util.List;

import com.hieunguyen.podcastai.enums.ErrorCode;
import com.hieunguyen.podcastai.exception.AppException;
import com.hieunguyen.podcastai.service.CategoryService;
import com.hieunguyen.podcastai.service.NewsSourceService;
import org.springframework.stereotype.Service;

import com.hieunguyen.podcastai.dto.request.FetchConfigurationRequest;
import com.hieunguyen.podcastai.dto.response.FetchConfigurationDto;
import com.hieunguyen.podcastai.entity.FetchConfiguration;
import com.hieunguyen.podcastai.mapper.FetchConfigurationMapper;
import com.hieunguyen.podcastai.repository.FetchConfigurationRepository;
import com.hieunguyen.podcastai.service.FetchConfigurationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FetchConfigurationServiceImpl implements FetchConfigurationService{

    private final FetchConfigurationRepository fetchConfigurationRepository;
    private final FetchConfigurationMapper fetchConfigurationMapper;
    private final CategoryService categoryService;
    private final NewsSourceService newsSourceService;

    @Override
    public FetchConfigurationDto createFetchConfiguration(FetchConfigurationRequest request) {

        if (request.getCategoryId() != null &&
                categoryService.getCategoryById(request.getCategoryId()) == null) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        if (newsSourceService.getSourceById(request.getNewsSourceId()) == null) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        FetchConfiguration fetchConfiguration = fetchConfigurationMapper.toEntity(request);

        return fetchConfigurationMapper.toDto(fetchConfigurationRepository.save(fetchConfiguration));
    }

    @Override
    public FetchConfigurationDto getFetchConfigurationById(Long id) {
        FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        return fetchConfigurationMapper.toDto(fetchConfiguration);
    }

    @Override
    @Transactional
    public FetchConfigurationDto updateFetchConfiguration(Long id, FetchConfigurationRequest request) {
        FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        fetchConfigurationMapper.updateEntity(request, fetchConfiguration);

        return fetchConfigurationMapper.toDto(fetchConfigurationRepository.save(fetchConfiguration));
    }

    @Override
    @Transactional
    public void deleteFetchConfiguration(Long id) {
        fetchConfigurationRepository.deleteById(id);
    }

    @Override
    public List<FetchConfigurationDto> getAllFetchConfigurations() {
        List<FetchConfiguration> fetchConfiguration = fetchConfigurationRepository.findAll();
        return fetchConfigurationMapper.toDtoList(fetchConfiguration);
    }
    
}
