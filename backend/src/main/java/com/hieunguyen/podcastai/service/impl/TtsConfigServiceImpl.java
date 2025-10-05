package com.hieunguyen.podcastai.service.impl;

import com.hieunguyen.podcastai.dto.request.TtsConfigRequest;
import com.hieunguyen.podcastai.dto.request.TtsConfigUpdateRequest;
import com.hieunguyen.podcastai.dto.response.TtsConfigDto;
import com.hieunguyen.podcastai.entity.TtsConfig;
import com.hieunguyen.podcastai.entity.User;
import com.hieunguyen.podcastai.enums.ErrorCode;
import com.hieunguyen.podcastai.exception.AppException;
import com.hieunguyen.podcastai.mapper.TtsConfigMapper;
import com.hieunguyen.podcastai.repository.TtsConfigRepository;
import com.hieunguyen.podcastai.service.TtsConfigService;
import com.hieunguyen.podcastai.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TtsConfigServiceImpl implements TtsConfigService {

    private final TtsConfigRepository ttsConfigRepository;
    private final TtsConfigMapper ttsConfigMapper;
    private final SecurityUtils securityUtils;

    @Override
    public TtsConfigDto createTtsConfig(TtsConfigRequest request) {
        log.info("Creating TTS configuration with name: {}", request.getName());
        
        User currentUser = securityUtils.getCurrentUser();
        
        // Check if name already exists for the user
        if (ttsConfigRepository.existsByUserAndNameAndIsActiveTrue(currentUser, request.getName())) {
            throw new AppException(ErrorCode.TTS_CONFIG_NAME_EXISTS);
        }
        
        // If this is set as default, unset other defaults
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultConfigs(currentUser);
        }
        
        TtsConfig ttsConfig = ttsConfigMapper.toEntity(request);
        ttsConfig.setUser(currentUser);
        
        TtsConfig savedTtsConfig = ttsConfigRepository.save(ttsConfig);
        log.info("Successfully created TTS configuration with ID: {}", savedTtsConfig.getId());
        
        return ttsConfigMapper.toDto(savedTtsConfig);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TtsConfigDto> getAllTtsConfigs() {
        log.info("Retrieving all TTS configurations for current user");
        
        User currentUser = securityUtils.getCurrentUser();
        List<TtsConfig> ttsConfigs = ttsConfigRepository.findByUserAndIsActiveTrueOrderByIsDefaultDescCreatedAtDesc(currentUser);
        
        return ttsConfigMapper.toDtoList(ttsConfigs);
    }

    @Override
    @Transactional(readOnly = true)
    public TtsConfigDto getTtsConfigById(Long id) {
        log.info("Retrieving TTS configuration with ID: {}", id);
        
        User currentUser = securityUtils.getCurrentUser();
        TtsConfig ttsConfig = ttsConfigRepository.findByIdAndUserAndIsActiveTrue(id, currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.TTS_CONFIG_NOT_FOUND));
        
        return ttsConfigMapper.toDto(ttsConfig);
    }

    @Override
    @Transactional(readOnly = true)
    public TtsConfigDto getDefaultTtsConfig() {
        log.info("Retrieving default TTS configuration for current user");
        
        User currentUser = securityUtils.getCurrentUser();
        TtsConfig defaultConfig = ttsConfigRepository.findByUserAndIsDefaultTrueAndIsActiveTrue(currentUser)
                .orElseThrow(() -> new AppException(ErrorCode.TTS_CONFIG_NO_DEFAULT));
        
        return ttsConfigMapper.toDto(defaultConfig);
    }

    @Override
    public TtsConfigDto updateTtsConfig(Long id, TtsConfigUpdateRequest request) {
        log.info("Updating TTS configuration with ID: {}", id);
        
        User currentUser = securityUtils.getCurrentUser();
        TtsConfig ttsConfig = ttsConfigRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TTS_CONFIG_NOT_FOUND));
        
        // Check if the TTS config belongs to the current user
        if (!ttsConfig.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.TTS_CONFIG_ACCESS_DENIED);
        }
        
        // Check if new name already exists (if name is being updated)
        if (request.getName() != null && !request.getName().equals(ttsConfig.getName())) {
            if (ttsConfigRepository.existsByUserAndNameAndIsActiveTrue(currentUser, request.getName())) {
                throw new AppException(ErrorCode.TTS_CONFIG_NAME_EXISTS);
            }
        }
        
        // If this is being set as default, unset other defaults
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultConfigs(currentUser);
        }
        
        ttsConfigMapper.updateEntity(request, ttsConfig);
        TtsConfig updatedTtsConfig = ttsConfigRepository.save(ttsConfig);
        
        log.info("Successfully updated TTS configuration with ID: {}", updatedTtsConfig.getId());
        return ttsConfigMapper.toDto(updatedTtsConfig);
    }

    @Override
    public void deleteTtsConfig(Long id) {
        log.info("Deleting TTS configuration with ID: {}", id);
        
        User currentUser = securityUtils.getCurrentUser();
        TtsConfig ttsConfig = ttsConfigRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TTS_CONFIG_NOT_FOUND));
        
        // Check if the TTS config belongs to the current user
        if (!ttsConfig.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.TTS_CONFIG_ACCESS_DENIED);
        }
        
        // Soft delete by setting isActive to false
        ttsConfig.setIsActive(false);
        ttsConfigRepository.save(ttsConfig);
        
        log.info("Successfully deleted TTS configuration with ID: {}", id);
    }

    @Override
    public TtsConfigDto setAsDefault(Long id) {
        log.info("Setting TTS configuration with ID: {} as default", id);
        
        User currentUser = securityUtils.getCurrentUser();
        TtsConfig ttsConfig = ttsConfigRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TTS_CONFIG_NOT_FOUND));
        
        // Check if the TTS config belongs to the current user
        if (!ttsConfig.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.TTS_CONFIG_ACCESS_DENIED);
        }
        
        // Unset other defaults
        unsetDefaultConfigs(currentUser);
        
        // Set this one as default
        ttsConfig.setIsDefault(true);
        TtsConfig updatedTtsConfig = ttsConfigRepository.save(ttsConfig);
        
        log.info("Successfully set TTS configuration with ID: {} as default", id);
        return ttsConfigMapper.toDto(updatedTtsConfig);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TtsConfigDto> getTtsConfigsByLanguage(String languageCode) {
        log.info("Retrieving TTS configurations by language: {}", languageCode);
        
        User currentUser = securityUtils.getCurrentUser();
        List<TtsConfig> ttsConfigs = ttsConfigRepository.findByUserAndLanguageCodeAndIsActiveTrueOrderByIsDefaultDescCreatedAtDesc(currentUser, languageCode);
        
        return ttsConfigMapper.toDtoList(ttsConfigs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TtsConfigDto> getTtsConfigsByVoice(String voiceName) {
        log.info("Retrieving TTS configurations by voice: {}", voiceName);
        
        User currentUser = securityUtils.getCurrentUser();
        List<TtsConfig> ttsConfigs = ttsConfigRepository.findByUserAndVoiceNameAndIsActiveTrueOrderByIsDefaultDescCreatedAtDesc(currentUser, voiceName);
        
        return ttsConfigMapper.toDtoList(ttsConfigs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TtsConfigDto> getTtsConfigsByAudioEncoding(String audioEncoding) {
        log.info("Retrieving TTS configurations by audio encoding: {}", audioEncoding);
        
        User currentUser = securityUtils.getCurrentUser();
        List<TtsConfig> ttsConfigs = ttsConfigRepository.findByUserAndAudioEncodingAndIsActiveTrueOrderByIsDefaultDescCreatedAtDesc(currentUser, audioEncoding);
        
        return ttsConfigMapper.toDtoList(ttsConfigs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TtsConfigDto> searchTtsConfigsByName(String name) {
        log.info("Searching TTS configurations by name: {}", name);
        
        User currentUser = securityUtils.getCurrentUser();
        List<TtsConfig> ttsConfigs = ttsConfigRepository.findByUserAndIsActiveTrueOrderByIsDefaultDescCreatedAtDesc(currentUser);
        
        // Filter by name (case-insensitive)
        List<TtsConfig> filteredConfigs = ttsConfigs.stream()
                .filter(config -> config.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
        
        return ttsConfigMapper.toDtoList(filteredConfigs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TtsConfigDto> getTtsConfigsBySpeakingRateRange(Double minRate, Double maxRate) {
        log.info("Retrieving TTS configurations by speaking rate range: {} - {}", minRate, maxRate);
        
        User currentUser = securityUtils.getCurrentUser();
        List<TtsConfig> ttsConfigs = ttsConfigRepository.findByUserAndSpeakingRateRange(currentUser, minRate, maxRate);
        
        return ttsConfigMapper.toDtoList(ttsConfigs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TtsConfigDto> getTtsConfigsByPitchRange(Double minPitch, Double maxPitch) {
        log.info("Retrieving TTS configurations by pitch range: {} - {}", minPitch, maxPitch);
        
        User currentUser = securityUtils.getCurrentUser();
        List<TtsConfig> ttsConfigs = ttsConfigRepository.findByUserAndPitchRange(currentUser, minPitch, maxPitch);
        
        return ttsConfigMapper.toDtoList(ttsConfigs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TtsConfigDto> getTtsConfigsByVolumeGainRange(Double minVolume, Double maxVolume) {
        log.info("Retrieving TTS configurations by volume gain range: {} - {}", minVolume, maxVolume);
        
        User currentUser = securityUtils.getCurrentUser();
        List<TtsConfig> ttsConfigs = ttsConfigRepository.findByUserAndVolumeGainRange(currentUser, minVolume, maxVolume);
        
        return ttsConfigMapper.toDtoList(ttsConfigs);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTtsConfigCount() {
        log.info("Retrieving TTS configuration count for current user");
        
        User currentUser = securityUtils.getCurrentUser();
        return ttsConfigRepository.countByUserAndIsActiveTrue(currentUser);
    }

    @Override
    public TtsConfigDto duplicateTtsConfig(Long id, String newName) {
        log.info("Duplicating TTS configuration with ID: {} with new name: {}", id, newName);
        
        User currentUser = securityUtils.getCurrentUser();
        TtsConfig originalConfig = ttsConfigRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TTS_CONFIG_NOT_FOUND));
        
        // Check if the TTS config belongs to the current user
        if (!originalConfig.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.TTS_CONFIG_ACCESS_DENIED);
        }
        
        // Check if new name already exists
        if (ttsConfigRepository.existsByUserAndNameAndIsActiveTrue(currentUser, newName)) {
            throw new AppException(ErrorCode.TTS_CONFIG_NAME_EXISTS);
        }
        
        // Create new TTS config with same settings but new name
        TtsConfig duplicatedConfig = TtsConfig.builder()
                .name(newName)
                .description(originalConfig.getDescription())
                .languageCode(originalConfig.getLanguageCode())
                .voiceName(originalConfig.getVoiceName())
                .speakingRate(originalConfig.getSpeakingRate())
                .pitch(originalConfig.getPitch())
                .volumeGainDb(originalConfig.getVolumeGainDb())
                .audioEncoding(originalConfig.getAudioEncoding())
                .sampleRateHertz(originalConfig.getSampleRateHertz())
                .isDefault(false) // Duplicated configs are not default
                .isActive(true)
                .user(currentUser)
                .build();
        
        TtsConfig savedConfig = ttsConfigRepository.save(duplicatedConfig);
        
        log.info("Successfully duplicated TTS configuration with ID: {}", savedConfig.getId());
        return ttsConfigMapper.toDto(savedConfig);
    }

    @Override
    public TtsConfigDto toggleActiveStatus(Long id) {
        log.info("Toggling active status for TTS configuration with ID: {}", id);
        
        User currentUser = securityUtils.getCurrentUser();
        TtsConfig ttsConfig = ttsConfigRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TTS_CONFIG_NOT_FOUND));
        
        // Check if the TTS config belongs to the current user
        if (!ttsConfig.getUser().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.TTS_CONFIG_ACCESS_DENIED);
        }
        
        // Toggle active status
        ttsConfig.setIsActive(!ttsConfig.getIsActive());
        
        // If deactivating and this was the default, unset it as default
        if (!ttsConfig.getIsActive() && ttsConfig.getIsDefault()) {
            ttsConfig.setIsDefault(false);
        }
        
        TtsConfig updatedTtsConfig = ttsConfigRepository.save(ttsConfig);
        
        log.info("Successfully toggled active status for TTS configuration with ID: {}", id);
        return ttsConfigMapper.toDto(updatedTtsConfig);
    }

    /**
     * Helper method to unset all default TTS configurations for a user
     */
    private void unsetDefaultConfigs(User user) {
        List<TtsConfig> defaultConfigs = ttsConfigRepository.findByUserAndIsDefaultTrueAndIsActiveTrue(user)
                .map(List::of)
                .orElse(List.of());
        
        for (TtsConfig config : defaultConfigs) {
            config.setIsDefault(false);
            ttsConfigRepository.save(config);
        }
    }
}
