package com.hieunguyen.podcastai.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hieunguyen.podcastai.entity.AudioFile;
import com.hieunguyen.podcastai.entity.NewsArticle;
import com.hieunguyen.podcastai.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioRepository extends JpaRepository<AudioFile, Long> {
    
    Page<AudioFile> findByUser(User user, Pageable pageable);

    Optional<AudioFile> findByNewsArticle(NewsArticle newsArticle);

}
