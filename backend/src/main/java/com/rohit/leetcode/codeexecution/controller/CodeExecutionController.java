package com.rohit.leetcode.codeexecution.controller;

import com.rohit.leetcode.codeexecution.dto.CodeExecutionRequest;
import com.rohit.leetcode.codeexecution.dto.CodeExecutionResponse;
import com.rohit.leetcode.codeexecution.service.CodeExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/code")
@RequiredArgsConstructor
public class CodeExecutionController {

    private final CodeExecutionService codeExecutionService;

    @PostMapping("/run")
    public CodeExecutionResponse runCode(
            @RequestBody CodeExecutionRequest request) {

        return codeExecutionService.execute(request);
    }
}