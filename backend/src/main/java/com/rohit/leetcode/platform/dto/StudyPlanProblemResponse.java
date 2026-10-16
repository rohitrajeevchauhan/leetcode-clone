package com.rohit.leetcode.platform.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyPlanProblemResponse {

    private Long problemId;

    private String title;

    private String difficulty;

    private String topic;

    private Integer problemOrder;

    private Boolean completed;
}