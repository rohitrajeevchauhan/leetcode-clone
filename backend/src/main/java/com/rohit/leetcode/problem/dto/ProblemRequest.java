package com.rohit.leetcode.problem.dto;

import com.rohit.leetcode.problem.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @NotBlank(message = "Description is required")
    private String description;

    private String constraints;

    private String example;

    private String starterCode;

    @NotBlank(message = "Topic is required")
    private String topic;

    private String tags;

    // =========================================================
    // LANGUAGE STARTER CODES
    // =========================================================

    private String javaStarterCode;

    private String cppStarterCode;

    private String pythonStarterCode;

    private String javascriptStarterCode;

    private String functionName;

    private String returnType;

    private String parameterDefinition;
}