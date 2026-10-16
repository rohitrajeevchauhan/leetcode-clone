package com.rohit.leetcode.codeexecution.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunCodeRequest {

    private Long problemId;

    private String sourceCode;

    private String language;

    private String versionIndex;
}