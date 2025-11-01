package com.hieunguyen.podcastai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TtsConfigDto {

    private Long id;
    private String name;
    private String description;
    private String languageCode;
    private String voiceName;
    private Double speakingRate;
    private Double pitch;
    private Double volumeGainDb;
    private String audioEncoding;
    private Integer sampleRateHertz;
    private Boolean isDefault;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
    private Long userId;
}
