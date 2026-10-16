package com.rohit.leetcode.problem.dto;

import com.rohit.leetcode.problem.entity.Difficulty;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemProgressResponse {

    private Long problemId;

    private String title;

    private Difficulty difficulty;

    private String topic;

    private String tags;

    /*
     * SOLVED
     * ATTEMPTED
     * NOT_STARTED
     */
    private String status;

    private long totalAttempts;

    private long acceptedAttempts;
}