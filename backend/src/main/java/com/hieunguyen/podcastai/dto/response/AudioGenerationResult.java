package com.hieunguyen.podcastai.dto.response;


@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class AudioGenerationResult {
    private String fileName;
    private String filePath;
    private Long fileSizeBytes;
    private Long durationSeconds;
    private byte[] audioBytes;
}
