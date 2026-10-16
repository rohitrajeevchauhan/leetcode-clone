package com.rohit.leetcode.platform.gamification.dto;

public record LeaderboardEntryResponse(
        int rank,
        Long userId,
        String username,
        long totalXp,
        int level,
        int problemsSolved
) {
}