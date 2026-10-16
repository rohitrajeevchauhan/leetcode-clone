package com.rohit.leetcode.run.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunCodeResponse {

    private Long problemId;

    private String language;

    private String status;

    private String output;

    private String errorMessage;

    private Long executionTime;

    private Long memoryUsed;

    private int totalTestCases;

    private int passedTestCases;

    private List<RunTestCaseResponse> testCases;
}