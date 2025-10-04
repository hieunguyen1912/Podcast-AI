package com.hieunguyen.podcastai.config;

import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class GoogleCloudTtsConfig {

    @Value("${google.cloud.tts.project-id:}")
    private String projectId;

    @Value("${google.cloud.tts.credentials.path:}")
    private String credentialsPath;

    @Value("${google.cloud.tts.timeout:30000}")
    private int timeoutMs;

    @Bean
    public TextToSpeechClient textToSpeechClient() throws IOException {
        log.info("Initializing Google Cloud Text-to-Speech client...");
        
        try {
            TextToSpeechSettings.Builder settingsBuilder = TextToSpeechSettings.newBuilder();
            
            if (!credentialsPath.isEmpty()) {
                log.info("Using credentials from path: {}", credentialsPath);
                System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", credentialsPath);
            }
            
            if (!projectId.isEmpty()) {
                log.info("Using project ID: {}", projectId);
                System.setProperty("GOOGLE_CLOUD_PROJECT", projectId);
            }
            
            TextToSpeechClient client = TextToSpeechClient.create(settingsBuilder.build());
            log.info("Google Cloud Text-to-Speech client initialized successfully");
            
            return client;
            
        } catch (Exception e) {
            log.error("Failed to initialize Google Cloud Text-to-Speech client: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Google Cloud TTS client", e);
        }
    }
}
