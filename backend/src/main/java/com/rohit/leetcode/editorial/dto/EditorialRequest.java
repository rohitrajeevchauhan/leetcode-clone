package com.rohit.leetcode.editorial.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditorialRequest {

    private Long problemId;

    @NotBlank(message = "Editorial title is required")
    private String title;

    @NotBlank(message = "Explanation is required")
    private String explanation;

    private String approach;
    private String algorithm;
    private String complexity;

    private String javaCode;
    private String cppCode;
    private String pythonCode;
    private String javascriptCode;

    private boolean published;
}