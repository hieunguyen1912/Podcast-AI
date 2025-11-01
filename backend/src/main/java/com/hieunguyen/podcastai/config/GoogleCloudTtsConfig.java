package com.hieunguyen.podcastai.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class GoogleCloudTtsConfig {

    @Value("${google.cloud.tts.project-id:}")
    private String projectId;

    @Value("${google.cloud.tts.credentials.path:}")
    private String credentialsPath;

    @PostConstruct
    public void init() throws Exception {
        log.info("========== SETTING UP CREDENTIALS ==========");

        // 1. Clear environment trước
        clearEnvironment("GOOGLE_APPLICATION_CREDENTIALS");

        // 2. Verify file exists
        Path credPath = Paths.get(credentialsPath);
        log.info("Credentials file: {}", credPath.toAbsolutePath());
        log.info("File exists: {}", Files.exists(credPath));

        if (!Files.exists(credPath)) {
            throw new FileNotFoundException("Credentials file not found: " + credPath.toAbsolutePath());
        }

        // 3. Set system property
        String absolutePath = credPath.toAbsolutePath().toString();
        System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", absolutePath);
        log.info("✓ System property set to: {}", absolutePath);
        log.info("✓ Verified: {}", System.getProperty("GOOGLE_APPLICATION_CREDENTIALS"));

        log.info("==========================================");
    }

    private void clearEnvironment(String key) {
        try {
            Class<?> cl = Class.forName("java.lang.ProcessEnvironment");
            java.lang.reflect.Field f = cl.getDeclaredField("theEnvironment");
            f.setAccessible(true);
            Object obj = f.get(null);

            @SuppressWarnings("unchecked")
            java.util.Map<String, String> map = (java.util.Map<String, String>) obj;
            map.remove(key);
            log.info("Cleared environment variable: {}", key);
        } catch (Exception e) {
            log.warn("Could not clear environment: {}", e.getMessage());
        }
    }

    @Bean
    public TextToSpeechClient textToSpeechClient() throws Exception {
        log.info("Creating TextToSpeechClient...");
        log.info("Current GOOGLE_APPLICATION_CREDENTIALS: {}", System.getProperty("GOOGLE_APPLICATION_CREDENTIALS"));

        try {
            // Tạo credentials object trực tiếp từ file
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new FileInputStream(credentialsPath)
            );

            TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            TextToSpeechClient client = TextToSpeechClient.create(settings);
            log.info("✓ TextToSpeechClient created successfully");
            return client;

        } catch (Exception e) {
            log.error("✗ Failed to create client: {}", e.getMessage(), e);
            throw e;
        }
    }
}