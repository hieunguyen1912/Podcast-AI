package com.hieunguyen.podcastai.entity;

import com.hieunguyen.podcastai.entity.base.AuditableEntity;
import com.hieunguyen.podcastai.enums.EpisodeStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "episodes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Episode extends AuditableEntity {
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "description", length = 2000)
    private String description;
    
    @Column(name = "episode_number")
    private Integer episodeNumber;
    
    @Column(name = "season_number")
    private Integer seasonNumber;
    
    @Column(name = "duration_seconds")
    private Long durationSeconds;
    
    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;
    
    @Column(name = "audio_file_url", nullable = false)
    private String audioFileUrl;
    
    @Column(name = "transcript_url")
    private String transcriptUrl;
    
    @Column(name = "summary", length = 1000)
    private String summary;
    
    @Column(name = "keywords", length = 500)
    private String keywords;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EpisodeStatus status = EpisodeStatus.DRAFT;
    
    @Column(name = "is_explicit", nullable = false)
    private Boolean isExplicit = false;
    
    @Column(name = "play_count")
    private Long playCount = 0L;
    
    @Column(name = "like_count")
    private Long likeCount = 0L;
    
    @Column(name = "download_count")
    private Long downloadCount = 0L;
    
    @Column(name = "published_at")
    private java.time.Instant publishedAt;
    
    // AI Processing fields
    @Column(name = "transcription_status")
    private String transcriptionStatus;
    
    @Column(name = "summarization_status")
    private String summarizationStatus;
    
    @Column(name = "translation_status")
    private String translationStatus;
    
    @Column(name = "tts_status")
    private String ttsStatus;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "podcast_id", nullable = false)
    private Podcast podcast;
    
    // @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<EpisodeVersion> versions = new ArrayList<>();
    
    // @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<EpisodePlay> plays = new ArrayList<>();
    
    
}