package com.hieunguyen.podcastai.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Voice settings for Google Cloud Text-to-Speech
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceSettingsRequest {

    @NotBlank(message = "Language code cannot be blank")
    private String languageCode;

    @NotBlank(message = "Voice name cannot be blank")
    private String voiceName;

    @NotNull(message = "Speaking rate cannot be null")
    @DecimalMin(value = "0.25", message = "Speaking rate must be at least 0.25")
    @DecimalMax(value = "4.0", message = "Speaking rate must not exceed 4.0")
    private Double speakingRate;

    @NotNull(message = "Pitch cannot be null")
    @DecimalMin(value = "-20.0", message = "Pitch must be at least -20.0")
    @DecimalMax(value = "20.0", message = "Pitch must not exceed 20.0")
    private Double pitch;

    @NotNull(message = "Volume gain cannot be null")
    @DecimalMin(value = "-96.0", message = "Volume gain must be at least -96.0")
    @DecimalMax(value = "16.0", message = "Volume gain must not exceed 16.0")
    private Double volumeGain;

    @Builder.Default
    private String audioEncoding = "MP3";

    @Builder.Default
    private Integer sampleRateHertz = 24000;
}
