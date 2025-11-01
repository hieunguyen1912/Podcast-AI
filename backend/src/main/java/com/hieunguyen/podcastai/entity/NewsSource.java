package com.hieunguyen.podcastai.entity;

import com.hieunguyen.podcastai.entity.base.BaseEntity;
import com.hieunguyen.podcastai.enums.NewsSourceType;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "news_sources")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsSource extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "url", length = 500)
    private String apiBaseUrl;

    @Column(name = "api_key", length = 500)
    private String apiKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NewsSourceType type;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String documentation;

    @Column(name = "last_success_at")
    private java.time.Instant lastSuccessAt;

    private String lastErrorMessage;

    @OneToMany(mappedBy = "newsSource", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FetchConfiguration> fetchConfigurations = new ArrayList<>();
}
