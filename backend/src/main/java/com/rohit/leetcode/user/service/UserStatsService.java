package com.rohit.leetcode.user.service;

import com.rohit.leetcode.user.dto.UserStatsResponse;

public interface UserStatsService {

    UserStatsResponse getMyStats();

    UserStatsResponse getUserStats(Long userId);
}