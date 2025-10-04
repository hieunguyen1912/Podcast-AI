package com.hieunguyen.podcastai.entity;

import com.hieunguyen.podcastai.entity.base.AuditableEntity;
import com.hieunguyen.podcastai.enums.Visibility;
import com.hieunguyen.podcastai.enums.PlaylistStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Playlist extends AuditableEntity {
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "cover_image_url")
    private String coverImageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    @Builder.Default
    private Visibility visibility = Visibility.PRIVATE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PlaylistStatus status = PlaylistStatus.ACTIVE;
    
    @Column(name = "total_audio_files_count")
    @Builder.Default
    private Integer totalAudioFilesCount = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
        name = "playlist_audio_files",
        joinColumns = @JoinColumn(name = "playlist_id"),
        inverseJoinColumns = @JoinColumn(name = "audio_file_id")
    )
    @Builder.Default
    private List<AudioFile> audioFiles = new ArrayList<>();


    public void addAudioFile(AudioFile audioFile) {
        this.audioFiles.add(audioFile);
        audioFile.getPlaylists().add(this);
    }
    
    public void removeAudioFile(AudioFile audioFile) {
        this.audioFiles.remove(audioFile);
        audioFile.getPlaylists().remove(this);
    }
}