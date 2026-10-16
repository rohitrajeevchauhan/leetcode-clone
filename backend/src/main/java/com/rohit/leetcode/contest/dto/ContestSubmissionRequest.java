package com.rohit.leetcode.contest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestSubmissionRequest {

    @NotNull(message = "Contest ID is required")
    private Long contestId;

    @NotNull(message = "Problem ID is required")
    private Long problemId;

    @NotBlank(message = "Source code is required")
    private String sourceCode;

    @NotBlank(message = "Language is required")
    private String language;
}