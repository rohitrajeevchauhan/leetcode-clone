package com.rohit.leetcode.codeexecution.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCaseResult {

    private Long testCaseId;

    private boolean passed;

    private String input;

    private String expectedOutput;

    private String actualOutput;

    private String error;

    private Long executionTime;
}