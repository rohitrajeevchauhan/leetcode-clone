package com.rohit.leetcode.discussion.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionResponse {

    private Long id;

    private Long problemId;

    private String problemTitle;

    private Long userId;

    private String username;

    private String title;

    private String content;

    private Integer likes;

    private Boolean likedByCurrentUser;

    private Integer commentCount;

    private Boolean closed;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}