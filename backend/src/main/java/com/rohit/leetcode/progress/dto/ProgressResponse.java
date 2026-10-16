package com.rohit.leetcode.progress.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressResponse {

    private long totalProblems;

    private long solvedProblems;

    private long attemptedProblems;

    private long unsolvedProblems;

    private long easyProblems;

    private long mediumProblems;

    private long hardProblems;

    private long easySolved;

    private long mediumSolved;

    private long hardSolved;

    private long totalSubmissions;

    private long acceptedSubmissions;

    private double acceptanceRate;
}