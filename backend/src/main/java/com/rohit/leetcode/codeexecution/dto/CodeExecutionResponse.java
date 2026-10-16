package com.rohit.leetcode.codeexecution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionResponse {

    private String stdout;

    private String stderr;

    private String compileOutput;

    private String status;
}