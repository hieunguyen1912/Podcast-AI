package com.hieunguyen.podcastai.repository;

import com.hieunguyen.podcastai.entity.TtsConfig;
import com.hieunguyen.podcastai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TtsConfigRepository extends JpaRepository<TtsConfig, Long> {
    
    /**
     * Find all TTS configurations for a specific user
     */
    List<TtsConfig> findByUserAndIsActiveTrueOrderByIsDefaultDescCreatedAtDesc(User user);
    
    /**
     * Find the default TTS configuration for a user
     */
    Optional<TtsConfig> findByUserAndIsDefaultTrueAndIsActiveTrue(User user);
    
    /**
     * Find TTS configuration by user and name
     */
    Optional<TtsConfig> findByUserAndNameAndIsActiveTrue(User user, String name);
    
    /**
     * Check if a TTS configuration name exists for a user
     */
    boolean existsByUserAndNameAndIsActiveTrue(User user, String name);
    
    /**
     * Find TTS configurations by language code for a user
     */
    List<TtsConfig> findByUserAndLanguageCodeAndIsActiveTrueOrderByIsDefaultDescCreatedAtDesc(User user, String languageCode);
    
    /**
     * Find TTS configurations by voice name for a user
     */
    List<TtsConfig> findByUserAndVoiceNameAndIsActiveTrueOrderByIsDefaultDescCreatedAtDesc(User user, String voiceName);
    
    /**
     * Count active TTS configurations for a user
     */
    long countByUserAndIsActiveTrue(User user);
    
    /**
     * Find TTS configurations with specific audio encoding for a user
     */
    List<TtsConfig> findByUserAndAudioEncodingAndIsActiveTrueOrderByIsDefaultDescCreatedAtDesc(User user, String audioEncoding);
    
    /**
     * Find TTS configurations within speaking rate range for a user
     */
    @Query("SELECT t FROM TtsConfig t WHERE t.user = :user AND t.isActive = true " +
           "AND t.speakingRate BETWEEN :minRate AND :maxRate " +
           "ORDER BY t.isDefault DESC, t.createdAt DESC")
    List<TtsConfig> findByUserAndSpeakingRateRange(@Param("user") User user, 
                                                   @Param("minRate") Double minRate, 
                                                   @Param("maxRate") Double maxRate);
    
    /**
     * Find TTS configurations within pitch range for a user
     */
    @Query("SELECT t FROM TtsConfig t WHERE t.user = :user AND t.isActive = true " +
           "AND t.pitch BETWEEN :minPitch AND :maxPitch " +
           "ORDER BY t.isDefault DESC, t.createdAt DESC")
    List<TtsConfig> findByUserAndPitchRange(@Param("user") User user, 
                                           @Param("minPitch") Double minPitch, 
                                           @Param("maxPitch") Double maxPitch);
    
    /**
     * Find TTS configurations within volume gain range for a user
     */
    @Query("SELECT t FROM TtsConfig t WHERE t.user = :user AND t.isActive = true " +
           "AND t.volumeGainDb BETWEEN :minVolume AND :maxVolume " +
           "ORDER BY t.isDefault DESC, t.createdAt DESC")
    List<TtsConfig> findByUserAndVolumeGainRange(@Param("user") User user, 
                                                @Param("minVolume") Double minVolume, 
                                                @Param("maxVolume") Double maxVolume);


    Optional<TtsConfig> findByIdAndUserAndIsActiveTrue(Long id, User user);

    Optional<TtsConfig> findByIdAndIsActiveTrue(Long id);

}
