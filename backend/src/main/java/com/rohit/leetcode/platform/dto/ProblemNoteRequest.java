package com.rohit.leetcode.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemNoteRequest {

    @NotBlank(message = "Note content is required")
    @Size(
            max = 10000,
            message = "Note cannot exceed 10000 characters"
    )
    private String content;
}