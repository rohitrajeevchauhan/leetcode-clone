package com.rohit.leetcode.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyPlanRequest {

    @NotBlank(message = "Plan title is required")
    @Size(
            max = 200,
            message = "Plan title cannot exceed 200 characters"
    )
    private String title;

    @Size(
            max = 1000,
            message = "Description cannot exceed 1000 characters"
    )
    private String description;

    @Size(
            max = 50,
            message = "Category cannot exceed 50 characters"
    )
    private String category;

    private String difficulty;

    private Integer estimatedDays;

    private Boolean publicPlan;
}