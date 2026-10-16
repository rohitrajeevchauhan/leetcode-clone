package com.rohit.leetcode.codeexecution.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunCodeResponse {

    private String status;

    private int totalTestCases;

    private int passedTestCases;

    private List<TestCaseResult> results;
}