package com.rohit.leetcode.platform.service;

import com.rohit.leetcode.platform.dto.UserProfileRequest;
import com.rohit.leetcode.platform.dto.UserProfileResponse;

public interface UserProfileService {

    UserProfileResponse getMyProfile();

    UserProfileResponse getProfile(String username);

    UserProfileResponse updateProfile(
            UserProfileRequest request
    );
}