package com.rohit.leetcode.platform.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkResponse {

    private Long id;

    private Long problemId;

    private String problemTitle;

    private String difficulty;

    private String topic;

    private LocalDateTime createdAt;
}