package com.rohit.leetcode.platform.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnalyticsResponse {

    private Long userId;

    private String username;

    private int totalProblemsAttempted;

    private int totalProblemsSolved;

    private int easySolved;

    private int mediumSolved;

    private int hardSolved;

    private int totalSubmissions;

    private int acceptedSubmissions;

    private double acceptanceRate;

    private int currentStreak;

    private int longestStreak;

    private int totalXp;

    private int level;

    private Map<String, Integer> problemsByTopic;

    private Map<String, Double> topicAccuracy;

    private Map<String, Integer> problemsByDifficulty;

    private Map<String, Integer> submissionsByLanguage;

    private Map<String, Integer> monthlyActivity;
}