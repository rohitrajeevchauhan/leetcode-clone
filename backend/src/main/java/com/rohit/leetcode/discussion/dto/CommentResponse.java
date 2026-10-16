package com.rohit.leetcode.discussion.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;

    private Long discussionId;

    private Long userId;

    private String username;

    private String content;

    private Integer likes;

    private Boolean likedByCurrentUser;

    private LocalDateTime createdAt;
}