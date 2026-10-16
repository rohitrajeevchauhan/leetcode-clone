package com.rohit.leetcode.community.dto;

import com.rohit.leetcode.community.entity.CommunitySolution;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunitySolutionRequest {

    @NotNull(message = "Problem ID is required")
    private Long problemId;

    @NotBlank(message = "Title is required")
    @Size(
            max = 200,
            message = "Title cannot exceed 200 characters"
    )
    private String title;

    @NotNull(message = "Language is required")
    private CommunitySolution.ProgrammingLanguage language;

    @NotBlank(message = "Code is required")
    private String code;

    private String explanation;

    @Size(max = 100)
    private String timeComplexity;

    @Size(max = 100)
    private String spaceComplexity;
}