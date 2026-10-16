package com.rohit.leetcode.achievement.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementResponse {

    private Long id;

    private String code;

    private String name;

    private String description;

    private String icon;

    private Integer xpReward;

    private boolean unlocked;
}