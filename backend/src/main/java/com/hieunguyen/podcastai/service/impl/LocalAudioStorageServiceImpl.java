package com.hieunguyen.podcastai.service.impl;

import com.hieunguyen.podcastai.entity.AudioFile;
import com.hieunguyen.podcastai.service.AudioStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Local file system implementation of AudioStorageService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LocalAudioStorageServiceImpl implements AudioStorageService {

    @Value("${audio.storage.path:/tmp/podcastai/audio}")
    private String storageBasePath;

    @Override
    public String storeAudioFile(AudioFile audioFile, byte[] audioBytes) {
        try {
            // Create storage directory if it doesn't exist
            Path storageDir = Paths.get(storageBasePath);
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
                log.info("Created audio storage directory: {}", storageDir);
            }

            // Create file path: /storage/path/userId/audioId/filename
            Path userDir = storageDir.resolve("user_" + audioFile.getUser().getId());
            if (!Files.exists(userDir)) {
                Files.createDirectories(userDir);
            }

            Path audioDir = userDir.resolve("audio_" + audioFile.getId());
            if (!Files.exists(audioDir)) {
                Files.createDirectories(audioDir);
            }

            Path filePath = audioDir.resolve(audioFile.getFileName());
            
            // Write audio bytes to file
            Files.write(filePath, audioBytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            
            String relativePath = "/audio/user_" + audioFile.getUser().getId() + 
                                "/audio_" + audioFile.getId() + "/" + audioFile.getFileName();
            
            log.info("Stored audio file: {} ({} bytes) at path: {}", 
                    audioFile.getFileName(), audioBytes.length, filePath);
            
            return relativePath;
            
        } catch (IOException e) {
            log.error("Failed to store audio file {}: {}", audioFile.getFileName(), e.getMessage(), e);
            throw new RuntimeException("Failed to store audio file", e);
        }
    }

    @Override
    public InputStream getAudioFileStream(AudioFile audioFile) {
        try {
            Path filePath = getFullStoragePath(audioFile);
            
            if (!Files.exists(filePath)) {
                log.warn("Audio file not found in storage: {}", filePath);
                return null;
            }
            
            log.info("Retrieving audio file stream: {}", filePath);
            return Files.newInputStream(filePath);
            
        } catch (IOException e) {
            log.error("Failed to get audio file stream for {}: {}", audioFile.getFileName(), e.getMessage(), e);
            throw new RuntimeException("Failed to get audio file stream", e);
        }
    }

    @Override
    public byte[] getAudioFileBytes(AudioFile audioFile) {
        try {
            Path filePath = getFullStoragePath(audioFile);
            
            if (!Files.exists(filePath)) {
                log.warn("Audio file not found in storage: {}", filePath);
                return null;
            }
            
            byte[] audioBytes = Files.readAllBytes(filePath);
            log.info("Retrieved audio file bytes: {} ({} bytes)", audioFile.getFileName(), audioBytes.length);
            
            return audioBytes;
            
        } catch (IOException e) {
            log.error("Failed to get audio file bytes for {}: {}", audioFile.getFileName(), e.getMessage(), e);
            throw new RuntimeException("Failed to get audio file bytes", e);
        }
    }

    @Override
    public boolean deleteAudioFile(AudioFile audioFile) {
        try {
            Path filePath = getFullStoragePath(audioFile);
            
            if (!Files.exists(filePath)) {
                log.warn("Audio file not found for deletion: {}", filePath);
                return false;
            }
            
            boolean deleted = Files.deleteIfExists(filePath);
            
            if (deleted) {
                log.info("Deleted audio file: {}", filePath);
                
                // Try to delete empty directories
                Path audioDir = filePath.getParent();
                Path userDir = audioDir.getParent();
                
                try {
                    if (Files.list(audioDir).findAny().isEmpty()) {
                        Files.deleteIfExists(audioDir);
                        log.info("Deleted empty audio directory: {}", audioDir);
                    }
                    
                    if (Files.list(userDir).findAny().isEmpty()) {
                        Files.deleteIfExists(userDir);
                        log.info("Deleted empty user directory: {}", userDir);
                    }
                } catch (IOException e) {
                    log.warn("Failed to clean up empty directories: {}", e.getMessage());
                }
            }
            
            return deleted;
            
        } catch (IOException e) {
            log.error("Failed to delete audio file {}: {}", audioFile.getFileName(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean audioFileExists(AudioFile audioFile) {
        Path filePath = getFullStoragePath(audioFile);
        boolean exists = Files.exists(filePath);
        log.debug("Audio file exists check for {}: {}", audioFile.getFileName(), exists);
        return exists;
    }

    @Override
    public String getStoragePath(AudioFile audioFile) {
        return "/audio/user_" + audioFile.getUser().getId() + 
               "/audio_" + audioFile.getId() + "/" + audioFile.getFileName();
    }

    /**
     * Get the full storage path for an audio file
     */
    private Path getFullStoragePath(AudioFile audioFile) {
        return Paths.get(storageBasePath)
                .resolve("user_" + audioFile.getUser().getId())
                .resolve("audio_" + audioFile.getId())
                .resolve(audioFile.getFileName());
    }
}
