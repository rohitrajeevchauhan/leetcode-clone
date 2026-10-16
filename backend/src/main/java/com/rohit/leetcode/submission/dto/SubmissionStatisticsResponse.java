package com.rohit.leetcode.submission.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionStatisticsResponse {

    private Long userId;

    private long totalSubmissions;

    private long problemsSolved;

    private long easySolved;

    private long mediumSolved;

    private long hardSolved;

    private long accepted;

    private long wrongAnswer;

    private long compilationError;

    private long runtimeError;

    private long timeLimitExceeded;

    private long memoryLimitExceeded;
}