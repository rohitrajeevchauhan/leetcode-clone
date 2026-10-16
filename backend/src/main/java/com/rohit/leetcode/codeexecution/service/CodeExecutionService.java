package com.rohit.leetcode.codeexecution.service;

import com.rohit.leetcode.codeexecution.dto.CodeExecutionRequest;
import com.rohit.leetcode.codeexecution.dto.CodeExecutionResponse;

public interface CodeExecutionService {

    CodeExecutionResponse execute(CodeExecutionRequest request);

}