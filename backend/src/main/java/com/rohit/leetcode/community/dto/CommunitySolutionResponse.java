package com.rohit.leetcode.community.dto;

import com.rohit.leetcode.community.entity.CommunitySolution;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunitySolutionResponse {

    private Long id;

    private Long problemId;

    private String problemTitle;

    private Long userId;

    private String username;

    private String title;

    private CommunitySolution.ProgrammingLanguage language;

    private String code;

    private String explanation;

    private String timeComplexity;

    private String spaceComplexity;

    private Integer likes;

    private Boolean likedByCurrentUser;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}