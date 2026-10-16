package com.rohit.leetcode.dashboard.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentSubmissionResponse {

    private Long submissionId;

    private Long problemId;

    private String problemTitle;

    private String language;

    private String status;

    private Long executionTime;

    private LocalDateTime createdAt;
}