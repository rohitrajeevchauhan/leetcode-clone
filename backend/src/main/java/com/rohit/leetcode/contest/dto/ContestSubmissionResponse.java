package com.rohit.leetcode.contest.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestSubmissionResponse {

    private Long id;

    private Long contestId;

    private Long problemId;

    private String problemTitle;

    private String language;

    private String status;

    private String output;

    private String errorMessage;

    private Long executionTime;

    private Long memoryUsed;

    private Integer score;

    private Integer penalty;

    private Integer attemptNumber;

    private LocalDateTime createdAt;
}