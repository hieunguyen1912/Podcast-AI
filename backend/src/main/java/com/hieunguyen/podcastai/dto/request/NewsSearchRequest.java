package com.hieunguyen.podcastai.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NewsSearchRequest {
    private String title;
    private String status;
    private String category;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
