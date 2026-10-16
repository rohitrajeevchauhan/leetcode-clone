package com.rohit.leetcode.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {

    private Long userId;

    private String username;

    private long totalSubmissions;

    private long acceptedSubmissions;

    private long wrongAnswerSubmissions;

    private long compilationErrorSubmissions;

    private long runtimeErrorSubmissions;

    private long totalSolved;

    private long easySolved;

    private long mediumSolved;

    private long hardSolved;

    private double acceptanceRate;

    private long rank;
}