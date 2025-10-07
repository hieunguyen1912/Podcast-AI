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

   private VoiceSettingsRequest voiceSettings;
}
