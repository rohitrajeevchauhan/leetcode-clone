package com.rohit.leetcode.user.controller;

import com.rohit.leetcode.user.dto.UserStatsResponse;
import com.rohit.leetcode.user.service.UserStatsService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserStatsController {

    private final UserStatsService userStatsService;


    // =========================================================
    // MY STATISTICS
    // =========================================================

    @GetMapping("/me/stats")
    public UserStatsResponse getMyStats() {

        return userStatsService.getMyStats();
    }


    // =========================================================
    // OTHER USER STATISTICS
    // =========================================================

    @GetMapping("/{userId}/stats")
    public UserStatsResponse getUserStats(
            @PathVariable Long userId) {

        return userStatsService.getUserStats(userId);
    }
}