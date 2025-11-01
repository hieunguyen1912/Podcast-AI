package com.hieunguyen.podcastai.dto.request;

import java.time.Instant;

import com.hieunguyen.podcastai.enums.FetchType;

import com.hieunguyen.podcastai.enums.SupportedCountry;
import com.hieunguyen.podcastai.enums.SupportedLanguage;
import com.hieunguyen.podcastai.validation.Language;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchConfigurationRequest {
    @NotNull(message = "News source ID is required")
    private Long newsSourceId;

    @NotNull(message = "Fetch type is required")
    private FetchType fetchType;

    private Boolean enabled;

    @Size(max = 200, message = "keywords must not exceed 200 characters")
    private String keywords;

    @Language
    private String languages;

    private SupportedCountry countries;

    private Long categoryId;

    @Min(1)
    @Max(100)
    private Integer maxResults;

    private Instant from;
    private Instant to;
}
