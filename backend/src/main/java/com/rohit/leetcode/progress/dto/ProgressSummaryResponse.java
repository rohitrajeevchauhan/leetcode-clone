package com.rohit.leetcode.progress.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressSummaryResponse {

    private long totalProblems;

    private long solved;

    private long attempted;

    private long notAttempted;

    private double acceptanceRate;

    private long easySolved;

    private long mediumSolved;

    private long hardSolved;

    private long totalSubmissions;

    private long acceptedSubmissions;
}