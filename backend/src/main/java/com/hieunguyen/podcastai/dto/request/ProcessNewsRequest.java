package com.hieunguyen.podcastai.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessNewsRequest {
    
    @NotBlank(message = "Query cannot be blank")
    private String query;
    
    @Size(min = 2, max = 5, message = "Language code must be 2-5 characters")
    private String language = "en";
    
    private String sortBy = "publishedAt";
    
    @Min(value = 1, message = "Max articles must be at least 1")
    @Max(value = 20, message = "Max articles cannot exceed 20")
    private int maxArticles = 10;
    
    @Min(value = 1000, message = "Max length must be at least 1000")
    @Max(value = 50000, message = "Max length cannot exceed 50000")
    private int maxLength = 5000;
}