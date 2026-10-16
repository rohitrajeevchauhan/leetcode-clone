package com.rohit.leetcode.achievement.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamificationResponse {

    private Long userId;

    private String username;

    private Long totalXp;

    private Integer level;

    private Long currentLevelXp;

    private Long nextLevelXp;

    private Integer currentStreak;

    private Integer longestStreak;

    private Integer problemsSolved;

    private Integer contestsParticipated;

    private Integer achievementsUnlocked;

    private Long xpToNextLevel;

    private Integer progressPercentage;
}