package com.rohit.leetcode.run.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunTestCaseResponse {

    private Long testCaseId;

    private boolean passed;

    private String input;

    private String expectedOutput;

    private String actualOutput;

    private String error;

    private Long executionTime;
}