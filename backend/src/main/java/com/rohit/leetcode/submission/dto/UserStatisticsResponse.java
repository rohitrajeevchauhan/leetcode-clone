package com.rohit.leetcode.submission.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatisticsResponse {

    private Long userId;

    private long totalSubmissions;

    private long accepted;

    private long wrongAnswer;

    private long compilationError;

    private long runtimeError;

    private long timeLimitExceeded;

    private long memoryLimitExceeded;
}