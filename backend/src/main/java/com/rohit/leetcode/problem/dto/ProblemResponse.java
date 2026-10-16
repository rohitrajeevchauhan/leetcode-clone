package com.rohit.leetcode.problem.dto;

import com.rohit.leetcode.problem.entity.Difficulty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemResponse {

    private Long id;

    private String title;

    private Difficulty difficulty;

    private String description;

    private String constraints;

    private String example;

    private String starterCode;

    private String topic;

    private String tags;

    private String status;

    private String javaStarterCode;

    private String cppStarterCode;

    private String pythonStarterCode;

    private String javascriptStarterCode;

    private long totalSubmissions;

    private long acceptedSubmissions;

    private String functionName;

    private String returnType;

    private String parameterDefinition;
}