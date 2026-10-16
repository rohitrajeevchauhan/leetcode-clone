package com.rohit.leetcode.platform.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyChallengeResponse {

    private Long id;

    private LocalDate challengeDate;

    private Long problemId;

    private String problemTitle;

    private String difficulty;

    private String topic;

    private boolean completed;

    private LocalDateTime completedAt;

    private int currentStreak;

    private int longestStreak;

    private int xpEarned;
}