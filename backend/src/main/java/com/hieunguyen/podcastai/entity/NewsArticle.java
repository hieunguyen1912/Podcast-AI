package com.hieunguyen.podcastai.entity;

import com.hieunguyen.podcastai.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "news_articles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NewsArticle extends AuditableEntity {

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "url", nullable = false, length = 1000, unique = true)
    private String url;

    @Column(name = "source_name", length = 200)
    private String sourceName;

    @Column(name = "source_url", length = 1000)
    private String sourceUrl;

    @Column(name = "author", length = 200)
    private String author;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "en";

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Long likeCount = 0L;

    @Column(name = "share_count", nullable = false)
    @Builder.Default
    private Long shareCount = 0L;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_source_id")
    private NewsSource newsSource;

    @OneToMany(mappedBy = "newsArticle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AudioFile> audioFiles = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "news_article_tags",
        joinColumns = @JoinColumn(name = "news_article_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();
}
