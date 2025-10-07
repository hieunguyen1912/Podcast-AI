package com.hieunguyen.podcastai.controller;

import com.google.cloud.texttospeech.v1.Voice;
import com.hieunguyen.podcastai.dto.request.GoogleTtsRequest;
import com.hieunguyen.podcastai.dto.request.VoiceSettingsRequest;
import com.hieunguyen.podcastai.dto.response.GoogleTtsResponse;
import com.hieunguyen.podcastai.service.GoogleTtsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Google Cloud Text-to-Speech operations
 */
@RestController
@RequestMapping("/api/v1/tts")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class GoogleTtsController {

    private final GoogleTtsService googleTtsService;

    /**
     * Synthesizes text to speech and returns audio bytes
     * 
     * @param request the TTS request
     * @return synthesized audio bytes
     */
    @PostMapping("/synthesize")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<byte[]> synthesizeText(@Valid @RequestBody GoogleTtsRequest request) {
        log.info("Received TTS synthesis request for {} characters", request.getText().length());
        
        try {
            byte[] response = googleTtsService.synthesizeAudioBytes(request);
            
            // Determine content type based on audio encoding
            String contentType = getContentType(request.getAudioEncoding());
            String fileExtension = getFileExtension(request.getAudioEncoding());
            
            log.info("Successfully processed TTS synthesis request, audio size: {} bytes", response.length);
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"audio." + fileExtension + "\"")
                .body(response);
            
        } catch (Exception e) {
            log.error("Failed to synthesize text: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Synthesizes text with custom voice settings and returns audio bytes
     * 
     * @param text the text to synthesize
     * @param voiceSettings the voice configuration
     * @return synthesized audio bytes
     */
    @PostMapping("/synthesize-with-settings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<byte[]> synthesizeWithSettings(
            @RequestParam String text,
            @Valid @RequestBody VoiceSettingsRequest voiceSettings) {
        
        log.info("Received TTS synthesis request with custom settings for {} characters", text.length());
        
        try {
            byte[] response = googleTtsService.synthesizeAudioBytes(
                GoogleTtsRequest.builder()
                    .text(text)
                    .languageCode(voiceSettings.getLanguageCode())
                    .voiceName(voiceSettings.getVoiceName())
                    .speakingRate(voiceSettings.getSpeakingRate())
                    .pitch(voiceSettings.getPitch())
                    .volumeGain(voiceSettings.getVolumeGain())
                    .audioEncoding(voiceSettings.getAudioEncoding())
                    .sampleRateHertz(voiceSettings.getSampleRateHertz())
                    .build()
            );
            
            // Determine content type based on audio encoding
            String contentType = getContentType(voiceSettings.getAudioEncoding());
            String fileExtension = getFileExtension(voiceSettings.getAudioEncoding());
            
            log.info("Successfully processed TTS synthesis request with custom settings, audio size: {} bytes", response.length);
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"audio." + fileExtension + "\"")
                .body(response);
            
        } catch (Exception e) {
            log.error("Failed to synthesize text with custom settings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gets available voices for a language
     * 
     * @param languageCode the language code
     * @return list of available voices
     */
    @GetMapping("/voices")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Voice>> getAvailableVoices(@RequestParam String languageCode) {
        log.info("Getting available voices for language: {}", languageCode);
        
        try {
            List<Voice> voices = googleTtsService.getAvailableVoices(languageCode);
            log.info("Found {} voices for language: {}", voices.size(), languageCode);
            return ResponseEntity.ok(voices);
            
        } catch (Exception e) {
            log.error("Failed to get available voices for language {}: {}", languageCode, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Validates voice settings
     * 
     * @param voiceSettings the voice settings to validate
     * @return validation result
     */
    @PostMapping("/validate-voice-settings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> validateVoiceSettings(@Valid @RequestBody VoiceSettingsRequest voiceSettings) {
        log.info("Validating voice settings for language: {} and voice: {}", 
                voiceSettings.getLanguageCode(), voiceSettings.getVoiceName());
        
        try {
            boolean isValid = googleTtsService.validateVoiceSettings(voiceSettings);
            log.info("Voice settings validation result: {}", isValid);
            return ResponseEntity.ok(isValid);
            
        } catch (Exception e) {
            log.error("Failed to validate voice settings: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Health check endpoint for TTS service
     * 
     * @return service status
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.debug("TTS service health check requested");
        return ResponseEntity.ok("Google Cloud TTS service is running");
    }

    /**
     * Gets supported languages
     * 
     * @return list of supported language codes
     */
    @GetMapping("/languages")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<String>> getSupportedLanguages() {
        log.info("Getting supported languages");
        
        try {
            // Common language codes supported by Google Cloud TTS
            List<String> languages = List.of(
                    "en-US", "en-GB", "en-AU", "en-CA", "en-IN",
                    "es-ES", "es-MX", "es-AR", "es-CO", "es-PE",
                    "fr-FR", "fr-CA", "fr-CH",
                    "de-DE", "de-AT", "de-CH",
                    "it-IT", "it-CH",
                    "pt-BR", "pt-PT",
                    "ja-JP", "ko-KR", "zh-CN", "zh-TW",
                    "vi-VN", "th-TH", "hi-IN", "ar-SA"
            );
            
            log.info("Returning {} supported languages", languages.size());
            return ResponseEntity.ok(languages);
            
        } catch (Exception e) {
            log.error("Failed to get supported languages: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get content type based on audio encoding
     */
    private String getContentType(String audioEncoding) {
        return switch (audioEncoding.toUpperCase()) {
            case "MP3" -> "audio/mpeg";
            case "WAV", "LINEAR16" -> "audio/wav";
            case "OGG_OPUS" -> "audio/ogg";
            case "MULAW" -> "audio/basic";
            case "ALAW" -> "audio/basic";
            default -> "audio/mpeg";
        };
    }
    
    /**
     * Get file extension based on audio encoding
     */
    private String getFileExtension(String audioEncoding) {
        return switch (audioEncoding.toUpperCase()) {
            case "MP3" -> "mp3";
            case "WAV", "LINEAR16" -> "wav";
            case "OGG_OPUS" -> "ogg";
            case "MULAW", "ALAW" -> "au";
            default -> "mp3";
        };
    }
}
