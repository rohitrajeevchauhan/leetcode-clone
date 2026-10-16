package com.rohit.leetcode.codeexecution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionRequest {

    private String sourceCode;

    private String language;

    private String versionIndex;

    private String stdin;
}