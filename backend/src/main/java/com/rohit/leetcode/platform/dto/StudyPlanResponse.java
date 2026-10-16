package com.rohit.leetcode.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanResponse {

    private Long id;

    private String title;

    private String description;

    private Integer durationDays;

    private String difficulty;

    private String topic;
}