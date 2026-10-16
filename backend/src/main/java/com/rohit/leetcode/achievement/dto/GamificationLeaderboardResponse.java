package com.rohit.leetcode.achievement.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamificationLeaderboardResponse {

    private int rank;

    private Long userId;

    private String username;

    private Long totalXp;

    private Integer level;

    private Integer problemsSolved;

    private Integer currentStreak;

    private Integer longestStreak;
}