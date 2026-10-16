package com.rohit.leetcode.achievement.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievementResponse {

    private Long achievementId;

    private String code;

    private String name;

    private String description;

    private String icon;

    private Integer xpReward;

    private LocalDateTime earnedAt;
}