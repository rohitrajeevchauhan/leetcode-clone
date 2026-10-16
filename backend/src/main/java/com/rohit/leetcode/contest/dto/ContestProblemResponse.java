package com.rohit.leetcode.contest.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestProblemResponse {

    private Long contestProblemId;

    private Long problemId;

    private String title;

    private String difficulty;

    private String topic;

    private Integer problemOrder;

    private Integer points;
}