package com.rohit.leetcode.favorite.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteProblemResponse {

    private Long problemId;

    private boolean favorite;
}