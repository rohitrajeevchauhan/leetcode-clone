package com.rohit.leetcode.contest.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestLeaderboardResponse {

    private Integer rank;

    private Long userId;

    private String username;

    private Integer totalScore;

    private Integer totalPenalty;

    private Integer solvedProblems;

    private Integer totalSubmissions;
}