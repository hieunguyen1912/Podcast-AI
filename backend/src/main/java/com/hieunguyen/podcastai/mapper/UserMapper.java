package com.hieunguyen.podcastai.mapper;

import com.hieunguyen.podcastai.dto.request.user.UserRegisterRequest;
import com.hieunguyen.podcastai.dto.request.user.UserUpdateRequest;
import com.hieunguyen.podcastai.dto.response.UserDto;
import com.hieunguyen.podcastai.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRegisterRequest request);

    User toEntity(UserUpdateRequest request);
    
    UserDto toDto(User user);
}
