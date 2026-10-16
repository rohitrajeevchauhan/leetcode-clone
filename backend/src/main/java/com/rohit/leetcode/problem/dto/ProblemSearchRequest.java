package com.rohit.leetcode.problem.dto;

import com.rohit.leetcode.problem.entity.Difficulty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemSearchRequest {

    private String search;

    private Difficulty difficulty;

    private String topic;

    private int page = 0;

    private int size = 20;

    private String sortBy = "id";

    private String direction = "ASC";
}