package com.rohit.leetcode.platform.controller;

import com.rohit.leetcode.platform.dto.UserProfileRequest;
import com.rohit.leetcode.platform.dto.UserProfileResponse;
import com.rohit.leetcode.platform.service.UserProfileService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platform/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    public UserProfileResponse getMyProfile() {

        return userProfileService.getMyProfile();
    }

    @GetMapping("/{username}")
    public UserProfileResponse getProfile(
            @PathVariable String username
    ) {

        return userProfileService.getProfile(username);
    }

    @PutMapping
    public UserProfileResponse updateProfile(
            @Valid @RequestBody UserProfileRequest request
    ) {

        return userProfileService.updateProfile(request);
    }
}