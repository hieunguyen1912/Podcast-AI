package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.dto.request.TtsConfigRequest;
import com.hieunguyen.podcastai.dto.request.TtsConfigUpdateRequest;
import com.hieunguyen.podcastai.dto.response.TtsConfigDto;

import java.util.List;

public interface TtsConfigService {

    /**
     * Create a new TTS configuration for the current user
     */
    TtsConfigDto createTtsConfig(TtsConfigRequest request);

    /**
     * Get all TTS configurations for the current user
     */
    List<TtsConfigDto> getAllTtsConfigs();

    /**
     * Get TTS configuration by ID for the current user
     */
    TtsConfigDto getTtsConfigById(Long id);

    /**
     * Get the default TTS configuration for the current user
     */
    TtsConfigDto getDefaultTtsConfig();

    /**
     * Update TTS configuration by ID for the current user
     */
    TtsConfigDto updateTtsConfig(Long id, TtsConfigUpdateRequest request);

    /**
     * Delete TTS configuration by ID for the current user
     */
    void deleteTtsConfig(Long id);

    /**
     * Set a TTS configuration as default for the current user
     */
    TtsConfigDto setAsDefault(Long id);

    /**
     * Get TTS configurations by language code for the current user
     */
    List<TtsConfigDto> getTtsConfigsByLanguage(String languageCode);

    /**
     * Get TTS configurations by voice name for the current user
     */
    List<TtsConfigDto> getTtsConfigsByVoice(String voiceName);

    /**
     * Get TTS configurations by audio encoding for the current user
     */
    List<TtsConfigDto> getTtsConfigsByAudioEncoding(String audioEncoding);

    /**
     * Search TTS configurations by name for the current user
     */
    List<TtsConfigDto> searchTtsConfigsByName(String name);

    /**
     * Get TTS configurations within speaking rate range for the current user
     */
    List<TtsConfigDto> getTtsConfigsBySpeakingRateRange(Double minRate, Double maxRate);

    /**
     * Get TTS configurations within pitch range for the current user
     */
    List<TtsConfigDto> getTtsConfigsByPitchRange(Double minPitch, Double maxPitch);

    /**
     * Get TTS configurations within volume gain range for the current user
     */
    List<TtsConfigDto> getTtsConfigsByVolumeGainRange(Double minVolume, Double maxVolume);

    /**
     * Get TTS configuration count for the current user
     */
    long getTtsConfigCount();

    /**
     * Duplicate TTS configuration by ID for the current user
     */
    TtsConfigDto duplicateTtsConfig(Long id, String newName);

    /**
     * Activate/Deactivate TTS configuration by ID for the current user
     */
    TtsConfigDto toggleActiveStatus(Long id);
}
