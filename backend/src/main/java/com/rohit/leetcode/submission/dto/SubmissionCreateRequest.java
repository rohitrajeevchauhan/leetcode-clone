package com.rohit.leetcode.submission.dto;

import com.rohit.leetcode.submission.entity.Submission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionCreateRequest {

    @NotNull(message = "Problem ID is required")
    private Long problemId;

    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "Programming language is required")
    private Submission.ProgrammingLanguage language;
}