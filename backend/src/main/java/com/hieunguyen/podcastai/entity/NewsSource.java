package com.hieunguyen.podcastai.entity;

import com.hieunguyen.podcastai.entity.base.BaseEntity;
import com.hieunguyen.podcastai.enums.NewsSourceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "news_sources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsSource extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name; // "News API", "GNews API", "BBC News", "VNExpress"

    @Column(name = "display_name", length = 100)
    private String displayName; // "BBC News", "VNExpress"

    @Column(name = "url", length = 500)
    private String url; // API endpoint or RSS URL

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NewsSourceType type; // API, RSS

    @Column(name = "api_key", length = 500)
    private String apiKey; // For API sources

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "priority", nullable = false)
    @Builder.Default
    private Integer priority = 0; // Higher priority = more important

    @Column(name = "update_interval_minutes", nullable = false)
    @Builder.Default
    private Integer updateIntervalMinutes = 60; // How often to fetch

    @Column(name = "max_articles_per_fetch", nullable = false)
    @Builder.Default
    private Integer maxArticlesPerFetch = 100;

    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "en";

    @Column(name = "country", length = 10)
    private String country; // "us", "vn", "gb"

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "last_fetch_at")
    private java.time.Instant lastFetchAt;

    @Column(name = "last_success_at")
    private java.time.Instant lastSuccessAt;

    @Column(name = "consecutive_failures", nullable = false)
    @Builder.Default
    private Integer consecutiveFailures = 0;

    @Column(name = "max_failures", nullable = false)
    @Builder.Default
    private Integer maxFailures = 5; // Disable after 5 consecutive failures
}
