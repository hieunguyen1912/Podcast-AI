package com.hieunguyen.podcastai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hieunguyen.podcastai.dto.response.ApiResponse;
import com.hieunguyen.podcastai.dto.response.UserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    // @GetMapping("/me")
    // public ResponseEntity<ApiResponse<UserDto>> getMe() {
    //     return ResponseEntity.ok(ApiResponse.success("User fetched successfully", userService.getMe()));
    // }
    
}
