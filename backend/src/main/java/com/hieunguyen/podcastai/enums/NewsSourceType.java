package com.hieunguyen.podcastai.enums;

public enum NewsSourceType {
    NEWS_API("News API"),
    GNEWS_API("GNews API"),
    MEDIASTACK_API("Mediastack API"),
    RSS_FEED("RSS Feed"),
    CUSTOM_API("Custom API");

    private final String displayName;

    NewsSourceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
