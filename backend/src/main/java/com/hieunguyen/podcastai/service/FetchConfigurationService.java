package com.hieunguyen.podcastai.service;

import java.util.List;

import com.hieunguyen.podcastai.dto.request.FetchConfigurationRequest;
import com.hieunguyen.podcastai.dto.response.FetchConfigurationDto;

public interface FetchConfigurationService {
    FetchConfigurationDto createFetchConfiguration(FetchConfigurationRequest request);
    FetchConfigurationDto getFetchConfigurationById(Long id);
    FetchConfigurationDto updateFetchConfiguration(Long id, FetchConfigurationRequest request);
    void deleteFetchConfiguration(Long id);
    List<FetchConfigurationDto> getAllFetchConfigurations();
}
