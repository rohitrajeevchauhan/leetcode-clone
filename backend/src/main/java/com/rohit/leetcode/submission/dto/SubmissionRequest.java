package com.rohit.leetcode.submission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionRequest {

    @NotNull(message = "Problem ID is required")
    private Long problemId;

    @NotBlank(message = "Source code is required")
    private String sourceCode;

    @NotBlank(message = "Language is required")
    private String language;

    private String versionIndex;
}