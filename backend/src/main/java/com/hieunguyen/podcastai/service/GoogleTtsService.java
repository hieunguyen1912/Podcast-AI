package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.dto.request.GoogleTtsRequest;
import com.hieunguyen.podcastai.dto.request.VoiceSettingsRequest;
import com.hieunguyen.podcastai.dto.response.GoogleTtsResponse;

public interface GoogleTtsService {

    GoogleTtsResponse synthesizeText(GoogleTtsRequest request);

    GoogleTtsResponse synthesizeTextWithSettings(String text, VoiceSettingsRequest voiceSettings);

    java.util.List<com.google.cloud.texttospeech.v1.Voice> getAvailableVoices(String languageCode);

    boolean validateVoiceSettings(VoiceSettingsRequest voiceSettings);

    byte[] synthesizeAudioBytes(GoogleTtsRequest request);
}
