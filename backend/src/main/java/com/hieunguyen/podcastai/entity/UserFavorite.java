package com.hieunguyen.podcastai.entity;

import com.hieunguyen.podcastai.entity.base.AuditableEntity;
import com.hieunguyen.podcastai.enums.FavoriteType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_favorites")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserFavorite extends AuditableEntity {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "favorite_type", nullable = false)
    private FavoriteType favoriteType;
    
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @Column(name = "notes", length = 500)
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audio_file_id")
    private AudioFile audioFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_article_id")
    private NewsArticle newsArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tts_config_id")
    private TtsConfig ttsConfig;
    
}