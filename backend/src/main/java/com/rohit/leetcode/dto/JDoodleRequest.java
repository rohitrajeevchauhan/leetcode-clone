package com.rohit.leetcode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JDoodleRequest {

    private String clientId;
    private String clientSecret;
    private String script;
    private String language;
    private String versionIndex;
    private String stdin;
}