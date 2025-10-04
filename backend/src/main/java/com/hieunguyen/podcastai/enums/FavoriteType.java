package com.hieunguyen.podcastai.enums;

public enum FavoriteType {
    AUDIO_FILE("Audio File"),
    PLAYLIST("Playlist"),
    TTS_CONFIG("TTS Config");

    private final String description;

    FavoriteType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}