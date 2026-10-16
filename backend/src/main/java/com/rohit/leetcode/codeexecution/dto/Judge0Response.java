package com.rohit.leetcode.codeexecution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Judge0Response {

    private String stdout;

    private String stderr;

    private String compile_output;

    private Status status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Status {
        private Integer id;
        private String description;
    }
}