package com.rohit.leetcode.submission.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponse {

    private Long id;

    private Long problemId;

    private String problemTitle;

    private String language;

    private String status;

    private String code;

    private String output;

    private String errorMessage;

    private Long executionTime;

    private Long memoryUsed;

    private String createdAt;

    private String updatedAt;

    private int totalTestCases;

    private int passedTestCases;

    private List<TestCaseResultResponse> testCases;
}