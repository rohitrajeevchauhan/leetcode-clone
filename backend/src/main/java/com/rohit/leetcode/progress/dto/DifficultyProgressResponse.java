package com.rohit.leetcode.progress.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DifficultyProgressResponse {

    private String difficulty;

    private long total;

    private long solved;

    private long attempted;

    private long notAttempted;

    private double progressPercentage;
}