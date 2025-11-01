package com.hieunguyen.podcastai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class NewsSourceDto {
    private Long id;
    private String name;
    private String description;
    private String apiBaseUrl;
    private Boolean isActive;
    private String apiKey;
    private Instant lastSuccessAt;
}
