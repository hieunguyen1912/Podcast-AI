package com.hieunguyen.podcastai.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NewsSourceRequest {
    private String name;
    private String apiBaseUrl;
    private String apiKey;
    private Boolean isActive;
    private String description;
    private String documentation;
}
