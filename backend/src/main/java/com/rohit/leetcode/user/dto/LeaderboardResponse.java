package com.rohit.leetcode.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardResponse {

    private Long rank;

    private Long userId;

    private String username;

    private long totalSolved;

    private long easySolved;

    private long mediumSolved;

    private long hardSolved;

    private long totalSubmissions;

    private long acceptedSubmissions;

    private double acceptanceRate;

    private long score;
}