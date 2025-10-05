package com.hieunguyen.podcastai.controller;

import com.hieunguyen.podcastai.dto.request.TtsConfigRequest;
import com.hieunguyen.podcastai.dto.request.TtsConfigUpdateRequest;
import com.hieunguyen.podcastai.dto.response.ApiResponse;
import com.hieunguyen.podcastai.dto.response.TtsConfigDto;
import com.hieunguyen.podcastai.service.TtsConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tts-configs")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class TtsConfigController {

    private final TtsConfigService ttsConfigService;

    /**
     * Create a new TTS configuration
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TtsConfigDto>> createTtsConfig(@Valid @RequestBody TtsConfigRequest request) {
        log.info("Creating TTS configuration with name: {}", request.getName());
        
        TtsConfigDto ttsConfig = ttsConfigService.createTtsConfig(request);
        
        ApiResponse<TtsConfigDto> response = ApiResponse.<TtsConfigDto>builder()
                .code(2000)
                .message("TTS configuration created successfully")
                .data(ttsConfig)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all TTS configurations for the current user
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TtsConfigDto>>> getAllTtsConfigs() {
        log.info("Retrieving all TTS configurations for current user");
        
        List<TtsConfigDto> ttsConfigs = ttsConfigService.getAllTtsConfigs();
        
        ApiResponse<List<TtsConfigDto>> response = ApiResponse.<List<TtsConfigDto>>builder()
                .code(2000)
                .message("TTS configurations retrieved successfully")
                .data(ttsConfigs)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get TTS configuration by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TtsConfigDto>> getTtsConfigById(@PathVariable Long id) {
        log.info("Retrieving TTS configuration with ID: {}", id);
        
        TtsConfigDto ttsConfig = ttsConfigService.getTtsConfigById(id);
        
        ApiResponse<TtsConfigDto> response = ApiResponse.<TtsConfigDto>builder()
                .code(2000)
                .message("TTS configuration retrieved successfully")
                .data(ttsConfig)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get the default TTS configuration
     */
    @GetMapping("/default")
    public ResponseEntity<ApiResponse<TtsConfigDto>> getDefaultTtsConfig() {
        log.info("Retrieving default TTS configuration for current user");
        
        TtsConfigDto ttsConfig = ttsConfigService.getDefaultTtsConfig();
        
        ApiResponse<TtsConfigDto> response = ApiResponse.<TtsConfigDto>builder()
                .code(2000)
                .message("Default TTS configuration retrieved successfully")
                .data(ttsConfig)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update TTS configuration by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TtsConfigDto>> updateTtsConfig(
            @PathVariable Long id, 
            @Valid @RequestBody TtsConfigUpdateRequest request) {
        log.info("Updating TTS configuration with ID: {}", id);
        
        TtsConfigDto ttsConfig = ttsConfigService.updateTtsConfig(id, request);
        
        ApiResponse<TtsConfigDto> response = ApiResponse.<TtsConfigDto>builder()
                .code(2000)
                .message("TTS configuration updated successfully")
                .data(ttsConfig)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete TTS configuration by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTtsConfig(@PathVariable Long id) {
        log.info("Deleting TTS configuration with ID: {}", id);
        
        ttsConfigService.deleteTtsConfig(id);
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(2000)
                .message("TTS configuration deleted successfully")
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Set TTS configuration as default
     */
    @PutMapping("/{id}/set-default")
    public ResponseEntity<ApiResponse<TtsConfigDto>> setAsDefault(@PathVariable Long id) {
        log.info("Setting TTS configuration with ID: {} as default", id);
        
        TtsConfigDto ttsConfig = ttsConfigService.setAsDefault(id);
        
        ApiResponse<TtsConfigDto> response = ApiResponse.<TtsConfigDto>builder()
                .code(2000)
                .message("TTS configuration set as default successfully")
                .data(ttsConfig)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get TTS configuration count
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getTtsConfigCount() {
        log.info("Retrieving TTS configuration count for current user");
        
        long count = ttsConfigService.getTtsConfigCount();
        
        ApiResponse<Long> response = ApiResponse.<Long>builder()
                .code(2000)
                .message("TTS configuration count retrieved successfully")
                .data(count)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Duplicate TTS configuration
     */
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<ApiResponse<TtsConfigDto>> duplicateTtsConfig(
            @PathVariable Long id, 
            @RequestParam String newName) {
        log.info("Duplicating TTS configuration with ID: {} with new name: {}", id, newName);
        
        TtsConfigDto ttsConfig = ttsConfigService.duplicateTtsConfig(id, newName);
        
        ApiResponse<TtsConfigDto> response = ApiResponse.<TtsConfigDto>builder()
                .code(2000)
                .message("TTS configuration duplicated successfully")
                .data(ttsConfig)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Toggle active status of TTS configuration
     */
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<TtsConfigDto>> toggleActiveStatus(@PathVariable Long id) {
        log.info("Toggling active status for TTS configuration with ID: {}", id);
        
        TtsConfigDto ttsConfig = ttsConfigService.toggleActiveStatus(id);
        
        ApiResponse<TtsConfigDto> response = ApiResponse.<TtsConfigDto>builder()
                .code(2000)
                .message("TTS configuration active status toggled successfully")
                .data(ttsConfig)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
