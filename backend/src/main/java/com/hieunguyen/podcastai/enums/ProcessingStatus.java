package com.hieunguyen.podcastai.enums;

/**
 * Enum for audio generation processing status
 */
public enum ProcessingStatus {
    PENDING,              // Waiting to be processed
    FETCHING_NEWS,        // Fetching content from News API
    PROCESSING_CONTENT,   // Processing content (summarization, translation)
    GENERATING_AUDIO,     // Generating audio with TTS
    COMPLETED,            // Successfully completed
    FAILED                // Processing failed
}

