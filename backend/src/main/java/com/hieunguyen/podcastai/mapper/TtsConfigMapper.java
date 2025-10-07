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

    /**
     * Convert TtsConfigRequest to TtsConfig entity
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "audioFiles", ignore = true)
    TtsConfig toEntity(TtsConfigRequest request);

    /**
     * Convert TtsConfig entity to TtsConfigDto
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = "instantToLocalDateTime")
    TtsConfigDto toDto(TtsConfig ttsConfig);

    /**
     * Convert list of TtsConfig entities to list of TtsConfigDto
     */
    List<TtsConfigDto> toDtoList(List<TtsConfig> ttsConfigs);

    /**
     * Update TtsConfig entity with TtsConfigUpdateRequest
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "audioFiles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(TtsConfigUpdateRequest request, @MappingTarget TtsConfig ttsConfig);

    /**
     * Convert TtsConfig entity to VoiceSettingsRequest
     */
    @Mapping(source = "volumeGainDb", target = "volumeGain")
    VoiceSettingsRequest toVoiceSettingsRequest(TtsConfig ttsConfig);

    /**
     * Convert VoiceSettingsRequest to TtsConfig entity (for creating new config)
     * Note: This returns a builder, needs user and other fields to be set
     */
    @Mapping(source = "volumeGain", target = "volumeGainDb")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "audioFiles", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "isDefault", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    TtsConfig toEntityFromVoiceSettings(VoiceSettingsRequest voiceSettings);

    /**
     * Convert Instant to LocalDateTime
     */
    @Named("instantToLocalDateTime")
    default LocalDateTime instantToLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
