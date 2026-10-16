package com.rohit.leetcode.codeexecution.controller;

import com.rohit.leetcode.codeexecution.dto.RunCodeRequest;
import com.rohit.leetcode.codeexecution.dto.RunCodeResponse;
import com.rohit.leetcode.codeexecution.service.RunCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/run")
@RequiredArgsConstructor
public class RunCodeController {

    private final RunCodeService runCodeService;

    @PostMapping
    public RunCodeResponse run(
            @RequestBody RunCodeRequest request
    ) {

        return runCodeService.run(request);
    }
}