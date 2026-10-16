package com.rohit.leetcode.problem.testcase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCaseRequest {

    @NotBlank(message = "Input cannot be empty")
    private String input;

    @NotBlank(message = "Expected output cannot be empty")
    private String expectedOutput;

    private boolean hidden;

    @NotNull(message = "Problem ID is required")
    private Long problemId;
}