package com.hieunguyen.podcastai.entity;

import java.util.ArrayList;
import java.util.List;

import com.hieunguyen.podcastai.entity.base.AuditableEntity;
import com.hieunguyen.podcastai.enums.ProcessingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "audio_files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AudioFile extends AuditableEntity {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "original_text", columnDefinition = "TEXT", nullable = false)
    private String originalText;

    @Column(name = "source_url", length = 500)
    private String sourceUrl; // URL của bài báo, RSS feed, etc.

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Column(name = "play_count", nullable = false)
    private Long playCount = 0L;

    @Column(name = "download_count", nullable = false)
    private Long downloadCount = 0L;

    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProcessingStatus status = ProcessingStatus.PENDING;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "published_at")
    private java.time.Instant publishedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tts_config_id", nullable = false)
    private TtsConfig ttsConfig;

    @ManyToMany(mappedBy = "audioFiles", fetch = FetchType.LAZY)
    private List<Playlist> playlists = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "audio_file_tags",
        joinColumns = @JoinColumn(name = "audio_file_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "audioFile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserFavorite> favorites = new ArrayList<>();
}