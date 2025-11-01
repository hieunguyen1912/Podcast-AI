package com.hieunguyen.podcastai.mapper;

import com.hieunguyen.podcastai.dto.request.TtsConfigRequest;
import com.hieunguyen.podcastai.dto.request.TtsConfigUpdateRequest;
import com.hieunguyen.podcastai.dto.request.VoiceSettingsRequest;
import com.hieunguyen.podcastai.dto.response.TtsConfigDto;
import com.hieunguyen.podcastai.entity.TtsConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TtsConfigMapper {

    TtsConfig toEntity(TtsConfigRequest request);

    TtsConfigDto toDto(TtsConfig ttsConfig);

    List<TtsConfigDto> toDtoList(List<TtsConfig> ttsConfigs);

    void updateEntity(TtsConfigUpdateRequest request, @MappingTarget TtsConfig ttsConfig);

    VoiceSettingsRequest toVoiceSettingsRequest(TtsConfig ttsConfig);

    TtsConfig toEntityFromVoiceSettings(VoiceSettingsRequest voiceSettings);

    @Named("instantToLocalDateTime")
    default LocalDateTime instantToLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
