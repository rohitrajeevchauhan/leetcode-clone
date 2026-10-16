package com.rohit.leetcode.progress.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemProgressResponse {

    private Long problemId;

    private String title;

    private String difficulty;

    private String topic;

    private String tags;

    private String status;

    private long totalSubmissions;

    private long acceptedSubmissions;

    private double acceptanceRate;
}