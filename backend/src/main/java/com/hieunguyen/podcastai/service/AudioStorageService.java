package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.entity.AudioFile;

import java.io.InputStream;

/**
 * Service for managing audio file storage
 */
public interface AudioStorageService {
    
    /**
     * Store audio file and return the file path
     * 
     * @param audioFile the audio file entity
     * @param audioBytes the audio file bytes
     * @return the stored file path
     */
    String storeAudioFile(AudioFile audioFile, byte[] audioBytes);
    
    /**
     * Retrieve audio file as input stream
     * 
     * @param audioFile the audio file entity
     * @return input stream of the audio file
     */
    InputStream getAudioFileStream(AudioFile audioFile);
    
    /**
     * Retrieve audio file as byte array
     * 
     * @param audioFile the audio file entity
     * @return byte array of the audio file
     */
    byte[] getAudioFileBytes(AudioFile audioFile);
    
    /**
     * Delete audio file from storage
     * 
     * @param audioFile the audio file entity
     * @return true if deleted successfully
     */
    boolean deleteAudioFile(AudioFile audioFile);
    
    /**
     * Check if audio file exists in storage
     * 
     * @param audioFile the audio file entity
     * @return true if file exists
     */
    boolean audioFileExists(AudioFile audioFile);
    
    /**
     * Get the storage path for an audio file
     * 
     * @param audioFile the audio file entity
     * @return the full storage path
     */
    String getStoragePath(AudioFile audioFile);
}
