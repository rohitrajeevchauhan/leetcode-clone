package com.rohit.leetcode.problem.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StarterCodeResponse {

    private Long problemId;

    private String language;

    private String starterCode;
}