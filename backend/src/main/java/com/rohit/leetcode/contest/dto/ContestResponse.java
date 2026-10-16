package com.rohit.leetcode.contest.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestResponse {

    private Long id;

    private String title;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer durationMinutes;

    private String status;

    private boolean published;

    private long problemCount;

    private long participantCount;

    private boolean registered;

    private LocalDateTime createdAt;
}