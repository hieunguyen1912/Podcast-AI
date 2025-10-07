package com.hieunguyen.podcastai.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hieunguyen.podcastai.entity.AudioFile;
import com.hieunguyen.podcastai.entity.User;

public interface AudioRepository extends JpaRepository<AudioFile, Long> {
    
    Optional<AudioFile> findByIdAndUser(Long id, User user);
    
    List<AudioFile> findByUserOrderByCreatedAtDesc(User user);
    
}
