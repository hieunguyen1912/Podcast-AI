package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.dto.request.AudioRequest;
import com.hieunguyen.podcastai.dto.response.AudioFileDto;
import com.hieunguyen.podcastai.entity.AudioFile;

import java.util.List;

public interface AudioService {

    AudioFileDto createAudio(AudioRequest request);
    
    AudioFile getAudioFileById(Long id);
    
    AudioFileDto getAudioFileDtoById(Long id);
    
    byte[] getAudioFileBytes(AudioFile audioFile);
    
    List<AudioFileDto> getUserAudioFiles();
    
    boolean deleteAudioFile(Long id);
    
}
