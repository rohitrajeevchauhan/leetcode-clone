package com.rohit.leetcode.problem.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemSearchResponse {

    private Long id;

    private String title;

    private String difficulty;

    private String topic;

    private String tags;

    private String status;

    private boolean favorite;

    private long totalSubmissions;

    private long acceptedSubmissions;
}