package com.hieunguyen.podcastai.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for voice settings in audio generation requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceSettingsDto {
    
    @NotBlank(message = "Language code is required")
    private String languageCode;        // e.g., "en-US", "vi-VN"
    
    @NotBlank(message = "Voice name is required")
    private String voiceName;          // e.g., "en-US-Standard-A"
    
    @NotNull(message = "Speaking rate is required")
    @DecimalMin(value = "0.25", message = "Speaking rate must be at least 0.25")
    @DecimalMax(value = "4.0", message = "Speaking rate must be at most 4.0")
    private Double speakingRate;       // 0.25 to 4.0
    
    @NotNull(message = "Pitch is required")
    @DecimalMin(value = "-20.0", message = "Pitch must be at least -20.0")
    @DecimalMax(value = "20.0", message = "Pitch must be at most 20.0")
    private Double pitch;              // -20.0 to 20.0 semitones
    
    @NotNull(message = "Volume gain is required")
    @DecimalMin(value = "-96.0", message = "Volume gain must be at least -96.0")
    @DecimalMax(value = "16.0", message = "Volume gain must be at most 16.0")
    private Double volumeGainDb;       // -96.0 to 16.0 dB
    
    @NotBlank(message = "Audio encoding is required")
    private String audioEncoding;      // "MP3", "WAV", "LINEAR16", "OGG_OPUS"
    
    // Optional processing flags
    @Builder.Default
    private Boolean isSummarized = false;      // Whether content is summarized
    
    @Builder.Default
    private Boolean isTranslated = false;      // Whether content is translated
    
    private String targetLanguage;     // Target language for translation
}
