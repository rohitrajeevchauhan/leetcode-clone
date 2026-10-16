package com.rohit.leetcode.favorite.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponse {

    private Long favoriteId;

    private Long problemId;

    private String problemTitle;

    private String difficulty;

    private String topic;

    private LocalDateTime createdAt;
}