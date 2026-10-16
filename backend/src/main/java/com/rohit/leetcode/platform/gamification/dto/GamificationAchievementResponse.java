package com.rohit.leetcode.platform.gamification.dto;

import com.rohit.leetcode.platform.gamification.entity.GamificationAchievement;

import java.time.LocalDateTime;

public record GamificationAchievementResponse(
        String achievementCode,
        String title,
        String description,
        int xpReward,
        LocalDateTime unlockedAt
) {
    public static GamificationAchievementResponse from(
            GamificationAchievement achievement
    ) {
        return new GamificationAchievementResponse(
                achievement.getAchievementCode(),
                achievement.getTitle(),
                achievement.getDescription(),
                achievement.getXpReward(),
                achievement.getUnlockedAt()
        );
    }
}