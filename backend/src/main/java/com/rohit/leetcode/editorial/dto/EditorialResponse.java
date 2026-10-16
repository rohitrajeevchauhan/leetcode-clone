package com.rohit.leetcode.editorial.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditorialResponse {

    private Long id;

    private Long problemId;

    private String problemTitle;

    private String title;

    private String explanation;

    private String approach;

    private String algorithm;

    private String complexity;

    private String javaCode;

    private String cppCode;

    private String pythonCode;

    private String javascriptCode;

    private boolean published;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}