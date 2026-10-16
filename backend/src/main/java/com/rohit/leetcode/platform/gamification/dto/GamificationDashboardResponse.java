package com.rohit.leetcode.platform.gamification.dto;

import java.util.List;

public record GamificationDashboardResponse(
        long totalXp,
        int level,
        int levelProgress,
        int xpToNextLevel,
        int problemsSolved,
        int contestsCompleted,
        int correctQuizAnswers,
        List<GamificationAchievementResponse> achievements
) {
}