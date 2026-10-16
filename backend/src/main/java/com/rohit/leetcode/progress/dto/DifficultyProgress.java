package com.rohit.leetcode.progress.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DifficultyProgress {

    private String difficulty;

    private long totalProblems;

    private long solvedProblems;

    private long attemptedProblems;

    private long solvedPercentage;
}