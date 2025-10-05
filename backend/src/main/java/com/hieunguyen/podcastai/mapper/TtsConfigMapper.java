package com.hieunguyen.podcastai.mapper;

import com.hieunguyen.podcastai.dto.request.TtsConfigRequest;
import com.hieunguyen.podcastai.dto.request.TtsConfigUpdateRequest;
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
    void updateEntity(TtsConfigUpdateRequest request, @MappingTarget TtsConfig ttsConfig);

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
