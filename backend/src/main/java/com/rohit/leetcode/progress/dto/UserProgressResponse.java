package com.rohit.leetcode.progress.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgressResponse {

    // =========================
    // PROBLEM STATISTICS
    // =========================

    private long totalProblems;

    private long attemptedProblems;

    private long solvedProblems;

    private long remainingProblems;


    // =========================
    // SUBMISSION STATISTICS
    // =========================

    private long totalSubmissions;

    private long acceptedSubmissions;

    private long wrongAnswerSubmissions;

    private long compilationErrorSubmissions;

    private long runtimeErrorSubmissions;

    private double acceptanceRate;


    // =========================
    // DIFFICULTY
    // =========================

    private DifficultyProgress easy;

    private DifficultyProgress medium;

    private DifficultyProgress hard;


    // =========================
    // TOPICS
    // =========================

    private List<TopicProgress> topicProgress;
}