package com.rohit.leetcode.submission.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCaseResultResponse {

    private Long testCaseId;

    private boolean passed;

    private String input;

    private String expectedOutput;

    private String actualOutput;

    private String error;

    private Long executionTime;
}