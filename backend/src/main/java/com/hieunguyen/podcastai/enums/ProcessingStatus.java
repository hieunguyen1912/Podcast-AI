package com.hieunguyen.podcastai.enums;

/**
 * Enum for audio generation processing status
 */
public enum ProcessingStatus {
    PENDING,
    FETCHING_NEWS,
    PROCESSING_CONTENT,
    GENERATING_AUDIO,
    COMPLETED,
    FAILED
}

