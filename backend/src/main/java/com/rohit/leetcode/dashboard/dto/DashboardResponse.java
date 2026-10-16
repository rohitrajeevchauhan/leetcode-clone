package com.rohit.leetcode.dashboard.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    // ==========================================
    // USER
    // ==========================================

    private Long userId;

    private String username;

    private String email;

    private String role;


    // ==========================================
    // PROBLEM STATISTICS
    // ==========================================

    private long totalProblems;

    private long attemptedProblems;

    private long solvedProblems;

    private long remainingProblems;


    // ==========================================
    // SUBMISSION STATISTICS
    // ==========================================

    private long totalSubmissions;

    private long acceptedSubmissions;

    private long wrongAnswerSubmissions;

    private long compilationErrorSubmissions;

    private long runtimeErrorSubmissions;

    private double acceptanceRate;


    // ==========================================
    // STREAK
    // ==========================================

    private int currentStreak;

    private int longestStreak;

    private long totalActiveDays;

    private boolean activeToday;


    // ==========================================
    // RECENT DATA
    // ==========================================

    private List<RecentSubmissionResponse> recentSubmissions;

    private List<RecentFavoriteResponse> recentFavorites;

    private List<DailyActivityResponse> dailyActivity;
}