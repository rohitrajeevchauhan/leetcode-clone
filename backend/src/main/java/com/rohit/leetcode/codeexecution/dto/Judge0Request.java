package com.rohit.leetcode.codeexecution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Judge0Request {

    private String source_code;

    private Integer language_id;

    private String stdin;
}