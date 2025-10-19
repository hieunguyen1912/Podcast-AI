package com.hieunguyen.podcastai.enums;

public enum NewsSourceType {
    NEWS_API("News API", "https://newsapi.org/v2"),
    GNEWS_API("GNews API", "https://gnews.io/api/v4"),
    MEDIASTACK_API("Mediastack API", "http://api.mediastack.com/v1"),
    RSS_FEED("RSS Feed", null),
    CUSTOM_API("Custom API", null);

    private final String displayName;
    private final String baseUrl;

    NewsSourceType(String displayName, String baseUrl) {
        this.displayName = displayName;
        this.baseUrl = baseUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean isApiSource() {
        return this != RSS_FEED;
    }

    public boolean isRssSource() {
        return this == RSS_FEED;
    }
}
