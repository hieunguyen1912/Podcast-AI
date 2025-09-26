package com.hieunguyen.podcastai.service;

import com.hieunguyen.podcastai.dto.request.UserLoginRequest;
import com.hieunguyen.podcastai.dto.request.UserRegisterRequest;
import com.hieunguyen.podcastai.dto.response.TokenDto;
import com.hieunguyen.podcastai.dto.response.UserLoginResponse;
import com.hieunguyen.podcastai.dto.response.UserRegisterResponse;
import com.hieunguyen.podcastai.entity.User;

public interface AuthService {
    UserRegisterResponse register(UserRegisterRequest request);
    UserLoginResponse login(UserLoginRequest request);
    TokenDto generateAccessToken(User user);
    TokenDto generateTokens(User user);
    TokenDto refreshToken(String token);
    void revokeRefreshToken(String refreshToken);
    void revokeAllUserTokens(User user);
}
