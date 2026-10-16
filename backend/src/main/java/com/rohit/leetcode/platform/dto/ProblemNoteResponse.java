package com.rohit.leetcode.platform.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemNoteResponse {

    private Long id;

    private Long problemId;

    private String problemTitle;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}