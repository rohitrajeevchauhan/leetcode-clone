package com.rohit.leetcode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JDoodleResponse {

    private String output;
    private String statusCode;
    private String memory;
    private String cpuTime;
    private String error;
}
