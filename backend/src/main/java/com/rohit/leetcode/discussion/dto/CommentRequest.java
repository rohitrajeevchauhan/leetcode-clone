package com.rohit.leetcode.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {

    @NotNull(message = "Discussion ID is required")
    private Long discussionId;

    @NotBlank(message = "Comment content is required")
    private String content;
}