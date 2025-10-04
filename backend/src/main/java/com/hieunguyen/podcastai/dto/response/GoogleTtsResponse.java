package com.hieunguyen.podcastai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Google Cloud Text-to-Speech API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTtsResponse {

    private String audioContent;
    private String audioEncoding;
    private Integer sampleRateHertz;
    private String languageCode;
    private String voiceName;
    private Double speakingRate;
    private Double pitch;
    private Double volumeGain;
    private LocalDateTime generatedAt;
    private Long durationMs;
    private String fileName;
    private String fileUrl;
}
