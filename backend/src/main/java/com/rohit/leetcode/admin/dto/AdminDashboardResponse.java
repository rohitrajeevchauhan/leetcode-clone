package com.rohit.leetcode.admin.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    // ==============================
    // USERS
    // ==============================

    private long totalUsers;

    private long activeUsers;


    // ==============================
    // PROBLEMS
    // ==============================

    private long totalProblems;

    private long easyProblems;

    private long mediumProblems;

    private long hardProblems;


    // ==============================
    // SUBMISSIONS
    // ==============================

    private long totalSubmissions;

    private long acceptedSubmissions;

    private long wrongAnswerSubmissions;

    private long compilationErrorSubmissions;

    private long runtimeErrorSubmissions;

    private double acceptanceRate;


    // ==============================
    // TEST CASES
    // ==============================

    private long totalTestCases;

    private long publicTestCases;

    private long hiddenTestCases;


    // ==============================
    // TOP PROBLEMS
    // ==============================

    private List<ProblemStatistic> mostAttemptedProblems;

    private List<ProblemStatistic> mostSolvedProblems;


    // ==============================
    // RECENT SUBMISSIONS
    // ==============================

    private List<RecentSubmission> recentSubmissions;


    // =========================================================
    // PROBLEM STATISTIC
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProblemStatistic {

        private Long problemId;

        private String title;

        private String difficulty;

        private long submissions;

        private long accepted;

        private double acceptanceRate;
    }


    // =========================================================
    // RECENT SUBMISSION
    // =========================================================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentSubmission {

        private Long submissionId;

        private Long problemId;

        private String problemTitle;

        private String username;

        private String language;

        private String status;

        private Long executionTime;

        private String createdAt;
    }
}