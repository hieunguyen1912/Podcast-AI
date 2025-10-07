package com.hieunguyen.podcastai.service.impl;

import com.google.cloud.texttospeech.v1.*;
import com.hieunguyen.podcastai.dto.request.GoogleTtsRequest;
import com.hieunguyen.podcastai.dto.request.VoiceSettingsRequest;
import com.hieunguyen.podcastai.dto.response.GoogleTtsResponse;
import com.hieunguyen.podcastai.service.GoogleTtsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleTtsServiceImpl implements GoogleTtsService {

    private final TextToSpeechClient textToSpeechClient;

    @Override
    public GoogleTtsResponse synthesizeText(GoogleTtsRequest request) {
        log.info("Synthesizing text with Google Cloud TTS: {} characters", request.getText().length());
        
        try {
            SynthesizeSpeechResponse response = performSynthesis(request);
            
            String fileName = generateFileName(request.getVoiceSettings().getAudioEncoding());
            
            log.info("Successfully synthesized text to speech. Audio size: {} bytes", 
                    response.getAudioContent().size());

            return GoogleTtsResponse.builder()
                    .audioContent(response.getAudioContent().toStringUtf8())
                    .audioEncoding(request.getVoiceSettings().getAudioEncoding())
                    .sampleRateHertz(request.getVoiceSettings().getSampleRateHertz())
                    .languageCode(request.getVoiceSettings().getLanguageCode())
                    .voiceName(request.getVoiceSettings().getVoiceName())
                    .speakingRate(request.getVoiceSettings().getSpeakingRate())
                    .pitch(request.getVoiceSettings().getPitch())
                    .volumeGain(request.getVoiceSettings().getVolumeGain())
                    .generatedAt(LocalDateTime.now())
                    .durationMs(calculateDuration(request.getText(), request.getVoiceSettings().getSpeakingRate()))
                    .fileName(fileName)
                    .fileUrl("/api/v1/tts/audio/" + fileName)
                    .build();

        } catch (Exception e) {
            log.error("Failed to synthesize text with Google Cloud TTS: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to synthesize text to speech", e);
        }
    }

    @Override
    public byte[] synthesizeAudioBytes(GoogleTtsRequest request) {
        log.info("Synthesizing text with Google Cloud TTS: {} characters", request.getText().length());
        
        try {
            SynthesizeSpeechResponse response = performSynthesis(request);
            
            byte[] audioBytes = response.getAudioContent().toByteArray();
            log.info("Successfully synthesized audio bytes, size: {} bytes", audioBytes.length);

            return audioBytes;

        } catch (Exception e) {
            log.error("Failed to synthesize text with Google Cloud TTS: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to synthesize text to speech", e);
        }
    }

    private SynthesizeSpeechResponse performSynthesis(GoogleTtsRequest request) {
        try {
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(request.getText())
                    .build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(request.getVoiceSettings().getLanguageCode())
                    .setName(request.getVoiceSettings().getVoiceName())
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.valueOf(request.getVoiceSettings().getAudioEncoding()))
                    .setSpeakingRate(request.getVoiceSettings().getSpeakingRate())
                    .setPitch(request.getVoiceSettings().getPitch())
                    .setVolumeGainDb(request.getVoiceSettings().getVolumeGain())
                    .setSampleRateHertz(request.getVoiceSettings().getSampleRateHertz())
                    .build();

            SynthesizeSpeechRequest synthesisRequest = SynthesizeSpeechRequest.newBuilder()
                    .setInput(input)
                    .setVoice(voice)
                    .setAudioConfig(audioConfig)
                    .build();

            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(synthesisRequest);
                        
            log.info("Successfully synthesized text to speech. Audio size: {} bytes", 
                    response.getAudioContent().size());

            return response;

        } catch (Exception e) {
            log.error("Failed to synthesize text with Google Cloud TTS: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to synthesize text to speech", e);
        }
    }

    @Override
    public GoogleTtsResponse synthesizeTextWithSettings(String text, VoiceSettingsRequest voiceSettings) {
        log.info("Synthesizing text with custom voice settings: {} characters", text.length());
        
        GoogleTtsRequest request = GoogleTtsRequest.builder()
                .text(text)
                .voiceSettings(voiceSettings)
                .build();

        return synthesizeText(request);
    }

    @Override
    public List<Voice> getAvailableVoices(String languageCode) {
        log.info("Getting available voices for language: {}", languageCode);
        
        try {
            ListVoicesRequest request = ListVoicesRequest.newBuilder()
                    .setLanguageCode(languageCode)
                    .build();

            ListVoicesResponse response = textToSpeechClient.listVoices(request);
            
            log.info("Found {} voices for language: {}", response.getVoicesCount(), languageCode);
            return response.getVoicesList();

        } catch (Exception e) {
            log.error("Failed to get available voices for language {}: {}", languageCode, e.getMessage(), e);
            throw new RuntimeException("Failed to get available voices", e);
        }
    }

    @Override
    public boolean validateVoiceSettings(VoiceSettingsRequest voiceSettings) {
        log.debug("Validating voice settings");
        
        try {
            List<Voice> voices = getAvailableVoices(voiceSettings.getLanguageCode());
            boolean voiceExists = voices.stream()
                    .anyMatch(voice -> voice.getName().equals(voiceSettings.getVoiceName()));
            
            if (!voiceExists) {
                log.warn("Voice {} not found for language {}", 
                        voiceSettings.getVoiceName(), voiceSettings.getLanguageCode());
                return false;
            }

            if (voiceSettings.getSpeakingRate() < 0.25 || voiceSettings.getSpeakingRate() > 4.0) {
                log.warn("Invalid speaking rate: {}", voiceSettings.getSpeakingRate());
                return false;
            }

            if (voiceSettings.getPitch() < -20.0 || voiceSettings.getPitch() > 20.0) {
                log.warn("Invalid pitch: {}", voiceSettings.getPitch());
                return false;
            }

            if (voiceSettings.getVolumeGain() < -96.0 || voiceSettings.getVolumeGain() > 16.0) {
                log.warn("Invalid volume gain: {}", voiceSettings.getVolumeGain());
                return false;
            }

            log.debug("Voice settings validation successful");
            return true;

        } catch (Exception e) {
            log.error("Failed to validate voice settings: {}", e.getMessage(), e);
            return false;
        }
    }

    private String generateFileName(String audioEncoding) {
        String extension = audioEncoding.toLowerCase();
        if (extension.equals("mp3")) {
            extension = "mp3";
        } else if (extension.equals("wav")) {
            extension = "wav";
        } else if (extension.equals("ogg")) {
            extension = "ogg";
        }
        
        return String.format("tts_%s.%s", UUID.randomUUID().toString(), extension);
    }

    private Long calculateDuration(String text, Double speakingRate) {
        // Rough estimation: 150 words per minute at normal rate
        int wordCount = text.split("\\s+").length;
        double wordsPerMinute = 150.0 * speakingRate;
        double durationMinutes = wordCount / wordsPerMinute;
        return Math.round(durationMinutes * 60 * 1000); // Convert to milliseconds
    }
}
