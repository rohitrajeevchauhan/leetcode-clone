package com.rohit.leetcode.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {

    private Long problemId;

    private String title;

    private String topic;

    private String difficulty;

    private String reason;
}