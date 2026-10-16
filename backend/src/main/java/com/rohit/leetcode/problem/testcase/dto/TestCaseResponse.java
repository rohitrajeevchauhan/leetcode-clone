package com.rohit.leetcode.problem.testcase.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCaseResponse {

    private Long id;

    private Long problemId;

    private String input;

    private String expectedOutput;

    private boolean hidden;
}