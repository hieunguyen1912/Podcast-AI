// package com.hieunguyen.podcastai.service.impl;

// import org.springframework.stereotype.Service;

// import com.hieunguyen.podcastai.dto.NewsArticle;
// import com.hieunguyen.podcastai.dto.request.AudioRequest;
// import com.hieunguyen.podcastai.dto.request.GoogleTtsRequest;
// import com.hieunguyen.podcastai.dto.request.VoiceSettingsRequest;
// import com.hieunguyen.podcastai.dto.response.AudioFileDto;
// import com.hieunguyen.podcastai.dto.response.AudioGenerationResult;
// import com.hieunguyen.podcastai.entity.AudioFile;
// import com.hieunguyen.podcastai.entity.Category;
// import com.hieunguyen.podcastai.entity.Tag;
// import com.hieunguyen.podcastai.entity.TtsConfig;
// import com.hieunguyen.podcastai.entity.User;
// import com.hieunguyen.podcastai.enums.ContentSource;
// import com.hieunguyen.podcastai.enums.ErrorCode;
// import com.hieunguyen.podcastai.enums.ProcessingStatus;
// import com.hieunguyen.podcastai.exception.AppException;
// import com.hieunguyen.podcastai.mapper.AudioMapper;
// import com.hieunguyen.podcastai.mapper.TtsConfigMapper;
// import com.hieunguyen.podcastai.repository.AudioRepository;
// import com.hieunguyen.podcastai.repository.CategoryRepository;
// import com.hieunguyen.podcastai.repository.TagRepository;
// import com.hieunguyen.podcastai.repository.TtsConfigRepository;
// import com.hieunguyen.podcastai.service.AudioService;
// import com.hieunguyen.podcastai.service.AudioStorageService;
// import com.hieunguyen.podcastai.service.GoogleTtsService;
// import com.hieunguyen.podcastai.service.NewsContentService;
// import com.hieunguyen.podcastai.util.SecurityUtils;

// import java.time.Instant;
// import java.util.List;
// import java.util.UUID;
// import java.util.stream.Collectors;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Service
// @Slf4j
// @RequiredArgsConstructor
// public class AudioServiceImple implements AudioService{

//     private final AudioRepository audioRepository;
//     private final SecurityUtils securityUtils;
//     private final TtsConfigRepository ttsConfigRepository;
//     private final NewsContentService newsContentService;
//     private final GoogleTtsService googleTtsService;
//     private final AudioMapper audioMapper;
//     private final TtsConfigMapper ttsConfigMapper;
//     private final AudioStorageService audioStorageService;
//     private final CategoryRepository categoryRepository;
//     private final TagRepository tagRepository;


//     @Override
//     public AudioFileDto createAudio(AudioRequest request) {
//         log.info("Creating audio with title: {}", request.getTitle());
        
//         try {
//             TtsConfig ttsConfig = getTtsConfiguration(request);
//             log.info("Using TTS config: {}", ttsConfig.getName());
            
//             String content = getContentFromRequest(request);
//             if (content == null || content.trim().isEmpty()) {
//                 log.error("No content retrieved for audio generation");
//                 throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
//             }
//             log.info("Retrieved content length: {} characters", content.length());
            
//             String processedContent = processContent(content, request);
//             if (processedContent == null || processedContent.trim().isEmpty()) {
//                 log.error("No processed content available for audio generation");
//                 throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
//             }
//             log.info("Processed content length: {} characters", processedContent.length());
            
//             AudioGenerationResult audioResult = generateAudio(processedContent, ttsConfig);
//             log.info("Generated audio: {} bytes, {} seconds", 
//                 audioResult.getFileSizeBytes(), audioResult.getDurationSeconds());
            
//             // 5. Create and save AudioFile entity
//             AudioFile audioFile = createAudioFileEntity(request, ttsConfig, audioResult, processedContent);
//             AudioFile savedAudio = audioRepository.save(audioFile);
//             log.info("Saved audio file with ID: {}", savedAudio.getId());
            
//             // 6. Store audio file in storage
//             String storagePath = audioStorageService.storeAudioFile(savedAudio, audioResult.getAudioBytes());
//             log.info("Stored audio file in storage: {}", storagePath);
            
//             // 7. Update file path in database
//             savedAudio.setFilePath(storagePath);
//             audioRepository.save(savedAudio);
            
//             // 8. Return DTO response
//             return audioMapper.toDto(savedAudio);
            
//         } catch (AppException e) {
//             log.error("App exception during audio creation: {}", e.getMessage(), e);
//             throw e;
//         } catch (Exception e) {
//             log.error("Unexpected error during audio creation: {}", e.getMessage(), e);
//             throw new AppException(ErrorCode.AUDIO_GENERATION_FAILED);
//         }
//     }

//     private TtsConfig getTtsConfiguration(AudioRequest request) {
//         if (request.getTtsConfigId() != null) {
//             return getExistingTtsConfig(request.getTtsConfigId());
//         } else if (request.getCustomVoiceSettings() != null) {
//             TtsConfig ttsConfig = ttsConfigMapper.toEntityFromVoiceSettings(request.getCustomVoiceSettings());
//             ttsConfig.setName("Custom Voice - " + System.currentTimeMillis());
//             ttsConfig.setDescription("Temporary voice settings for audio generation");
//             ttsConfig.setIsDefault(false);
//             ttsConfig.setIsActive(true);
//             ttsConfig.setUser(securityUtils.getCurrentUser());
//             return ttsConfig;
//         } else {
//             throw new AppException(ErrorCode.TTS_CONFIG_NOT_FOUND);
//         }
//     }

//     private TtsConfig getExistingTtsConfig(Long ttsConfigId) {
//         User currentUser = securityUtils.getCurrentUser();
        
//         return ttsConfigRepository.findByIdAndUserAndIsActiveTrue(ttsConfigId, currentUser)
//             .orElseThrow(() -> new AppException(ErrorCode.TTS_CONFIG_NOT_FOUND));
//     }

//     private String getContentFromRequest(AudioRequest request) {
//         if (request.getContentSource() == ContentSource.NEWS_API) {
//             return getNewsContent(request.getNewsArticleUrl());
//         } else if (request.getContentSource() == ContentSource.TEXT_INPUT) {
//             return request.getTextContent();
//         } else {
//             throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
//         }
//     }

//     private String getNewsContent(String newsUrl) {
//         try {
//             log.info("Fetching news content from URL: {}", newsUrl);
            
//             NewsArticle article = new NewsArticle();
//             article.setUrl(newsUrl);
//             article = newsContentService.getArticleWithFullContent(article);
            
//             if (article == null) {
//                 log.error("NewsContentService returned null article for URL: {}", newsUrl);
//                 throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
//             }
            
//             String fullContent = article.getFullContent();
//             if (fullContent == null || fullContent.trim().isEmpty()) {
//                 log.error("No content extracted from URL: {} - Full content: '{}'", newsUrl, fullContent);
//                 throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
//             }
            
//             log.info("Successfully extracted {} characters from news URL: {}", fullContent.length(), newsUrl);
//             return fullContent;
            
//         } catch (Exception e) {
//             log.error("Failed to get news content from URL: {} - Error: {}", newsUrl, e.getMessage(), e);
//             throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
//         }
//     }
    
   
//     private String processContent(String content, AudioRequest request) {
//         String processedContent = content;
//         // Apply summarization
//         if (request.getEnableSummarization()) {
//             processedContent = summarizeContent(processedContent);
//             log.info("Content summarized");
//         }
        
//         // Apply translation
//         if (request.getEnableTranslation() && request.getTargetLanguageCode() != null) {
//             processedContent = translateContent(processedContent, request.getTargetLanguageCode());
//             log.info("Content translated to {}", request.getTargetLanguageCode());
//         }
        
//         return processedContent;
//     }
   
//     private AudioGenerationResult generateAudio(String content, TtsConfig ttsConfig) {
//         try {
//             VoiceSettingsRequest voiceSettings = ttsConfigMapper.toVoiceSettingsRequest(ttsConfig);
//             GoogleTtsRequest ttsRequest = GoogleTtsRequest.builder()
//                 .text(content)
//                 .voiceSettings(voiceSettings)
//                 .build();
            
//             byte[] audioBytes = googleTtsService.synthesizeAudioBytes(ttsRequest);
            
//             long durationSeconds = Math.max(1, content.length() / 150);
            
//             String fileName = generateAudioFileName(ttsConfig.getAudioEncoding());
            
//             return AudioGenerationResult.builder()
//                 .fileName(fileName)
//                 .filePath("/audio/" + fileName)
//                 .fileSizeBytes((long) audioBytes.length)
//                 .durationSeconds(durationSeconds)
//                 .audioBytes(audioBytes)
//                 .build();
                
//         } catch (Exception e) {
//             log.error("Failed to generate audio: {}", e.getMessage(), e);
//             throw new AppException(ErrorCode.TTS_SYNTHESIS_FAILED);
//         }
//     }
    
//     private AudioFile createAudioFileEntity(AudioRequest request, TtsConfig ttsConfig, 
//                                           AudioGenerationResult audioResult, String processedContent) {
        
//         Category category = categoryRepository.findById(request.getCategoryId())
//             .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        
//         List<Tag> tags = List.of();
//         if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
//             tags = tagRepository.findAllById(request.getTagIds());
//         }
        
//         return AudioFile.builder()
//             .title(request.getTitle())
//             .description(request.getDescription())
//             .sourceUrl(request.getNewsArticleUrl())
//             .fileName(audioResult.getFileName())
//             .fileSizeBytes(audioResult.getFileSizeBytes())
//             .status(ProcessingStatus.COMPLETED)
//             .publishedAt(Instant.now())
//             .user(securityUtils.getCurrentUser())
//             .ttsConfig(ttsConfig)
//             .newsArticle(newsArticle)
//             .build();
//     }
    
//     private String generateAudioFileName(String audioEncoding) {
//         String extension = audioEncoding.toLowerCase();
//         if (extension.equals("linear16")) {
//             extension = "wav";
//         }
//         return "audio_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + extension;
//     }
    
//     private String summarizeContent(String content) {
//         return content;
//     }
    
//     private String translateContent(String content, String targetLanguage) {
//         return content;
//     }
    
//     @Override
//     public AudioFile getAudioFileById(Long id) {
//         log.info("Getting audio file by ID: {}", id);
        
//         User currentUser = securityUtils.getCurrentUser();
//         return audioRepository.findByIdAndUser(id, currentUser)
//             .orElseThrow(() -> new AppException(ErrorCode.AUDIO_FILE_NOT_FOUND));
//     }
    
//     @Override
//     public AudioFileDto getAudioFileDtoById(Long id) {
//         log.info("Getting audio file DTO by ID: {}", id);
        
//         AudioFile audioFile = getAudioFileById(id);
//         return audioMapper.toDto(audioFile);
//     }
    
//     @Override
//     public byte[] getAudioFileBytes(AudioFile audioFile) {
//         log.info("Getting audio file bytes for: {}", audioFile.getFileName());
        
//         try {
//             // First, try to get from storage
//             if (audioStorageService.audioFileExists(audioFile)) {
//                 log.info("Audio file found in storage, retrieving...");
//                 return audioStorageService.getAudioFileBytes(audioFile);
//             }
            
//             // If not in storage, regenerate and store
//             log.info("Audio file not found in storage, regenerating...");
//             TtsConfig ttsConfig = audioFile.getTtsConfig();
//             String originalText = audioFile.getOriginalText();
            
//             if (ttsConfig == null || originalText == null) {
//                 throw new AppException(ErrorCode.AUDIO_FILE_NOT_FOUND);
//             }
            
//             // Regenerate audio using the same TTS config and text
//             VoiceSettingsRequest voiceSettings = ttsConfigMapper.toVoiceSettingsRequest(ttsConfig);
//             GoogleTtsRequest ttsRequest = GoogleTtsRequest.builder()
//                 .text(originalText)
//                 .voiceSettings(voiceSettings)
//                 .build();
            
//             byte[] audioBytes = googleTtsService.synthesizeAudioBytes(ttsRequest);
            
//             // Store the regenerated audio
//             String storagePath = audioStorageService.storeAudioFile(audioFile, audioBytes);
//             audioFile.setFilePath(storagePath);
//             audioRepository.save(audioFile);
            
//             log.info("Regenerated and stored audio file: {}", storagePath);
//             return audioBytes;
            
//         } catch (Exception e) {
//             log.error("Failed to get audio file bytes: {}", e.getMessage(), e);
//             throw new AppException(ErrorCode.AUDIO_FILE_NOT_FOUND);
//         }
//     }
    
//     @Override
//     public List<AudioFileDto> getUserAudioFiles() {
//         log.info("Getting user's audio files");
        
//         User currentUser = securityUtils.getCurrentUser();
//         List<AudioFile> audioFiles = audioRepository.findByUserOrderByCreatedAtDesc(currentUser);
        
//         return audioFiles.stream()
//             .map(audioMapper::toDto)
//             .collect(Collectors.toList());
//     }
    
//     @Override
//     public boolean deleteAudioFile(Long id) {
//         log.info("Deleting audio file with ID: {}", id);
        
//         try {
//             AudioFile audioFile = getAudioFileById(id);
            
//             // Delete from storage
//             boolean storageDeleted = audioStorageService.deleteAudioFile(audioFile);
//             log.info("Storage deletion result: {}", storageDeleted);
            
//             // Delete from database
//             audioRepository.delete(audioFile);
//             log.info("Deleted audio file from database: {}", id);
            
//             return true;
            
//         } catch (Exception e) {
//             log.error("Failed to delete audio file with ID {}: {}", id, e.getMessage(), e);
//             return false;
//         }
//     }
// }
