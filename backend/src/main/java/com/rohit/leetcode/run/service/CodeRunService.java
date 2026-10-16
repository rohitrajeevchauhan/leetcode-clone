package com.rohit.leetcode.run.service;

import com.rohit.leetcode.run.dto.RunCodeRequest;
import com.rohit.leetcode.run.dto.RunCodeResponse;

public interface CodeRunService {

    RunCodeResponse runCode(RunCodeRequest request);
}