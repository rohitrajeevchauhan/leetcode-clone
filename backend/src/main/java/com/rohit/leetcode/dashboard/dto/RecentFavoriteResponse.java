package com.rohit.leetcode.dashboard.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentFavoriteResponse {

    private Long problemId;

    private String problemTitle;

    private String difficulty;

    private String topic;

    private LocalDateTime createdAt;
}