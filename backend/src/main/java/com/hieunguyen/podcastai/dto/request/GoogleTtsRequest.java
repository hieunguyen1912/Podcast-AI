package com.hieunguyen.podcastai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for Google Cloud Text-to-Speech API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTtsRequest {

    @NotBlank(message = "Text to synthesize cannot be blank")
    @Size(max = 5000, message = "Text cannot exceed 5000 characters")
    private String text;

    @NotBlank(message = "Language code cannot be blank")
    @Size(min = 2, max = 10, message = "Language code must be between 2 and 10 characters")
    private String languageCode;

    @NotBlank(message = "Voice name cannot be blank")
    private String voiceName;

    @NotNull(message = "Speaking rate cannot be null")
    private Double speakingRate;

    @NotNull(message = "Pitch cannot be null")
    private Double pitch;

    @NotNull(message = "Volume gain cannot be null")
    private Double volumeGain;

    @Builder.Default
    private String audioEncoding = "MP3";

    @Builder.Default
    private Integer sampleRateHertz = 24000;
}
