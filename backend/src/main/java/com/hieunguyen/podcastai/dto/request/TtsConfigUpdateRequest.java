package com.hieunguyen.podcastai.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TtsConfigUpdateRequest {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 10, message = "Language code must not exceed 10 characters")
    @Pattern(regexp = "^[a-z]{2}-[A-Z]{2}$", message = "Language code must be in format 'xx-XX'")
    private String languageCode;

    @Size(max = 50, message = "Voice name must not exceed 50 characters")
    private String voiceName;

    @DecimalMin(value = "0.25", message = "Speaking rate must be at least 0.25")
    @DecimalMax(value = "4.0", message = "Speaking rate must not exceed 4.0")
    private Double speakingRate;

    @DecimalMin(value = "-20.0", message = "Pitch must be at least -20.0")
    @DecimalMax(value = "20.0", message = "Pitch must not exceed 20.0")
    private Double pitch;

    @DecimalMin(value = "-96.0", message = "Volume gain must be at least -96.0")
    @DecimalMax(value = "16.0", message = "Volume gain must not exceed 16.0")
    private Double volumeGainDb;

    @Size(max = 20, message = "Audio encoding must not exceed 20 characters")
    @Pattern(regexp = "^(MP3|WAV|LINEAR16|OGG_OPUS|MULAW|ALAW)$", 
             message = "Audio encoding must be one of: MP3, WAV, LINEAR16, OGG_OPUS, MULAW, ALAW")
    private String audioEncoding;

    @Min(value = 8000, message = "Sample rate must be at least 8000")
    @Max(value = 48000, message = "Sample rate must not exceed 48000")
    private Integer sampleRateHertz;

    private Boolean isDefault;

    private Boolean isActive;
}
