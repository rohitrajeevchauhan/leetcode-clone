package com.rohit.leetcode.codeexecution.service;

import com.rohit.leetcode.codeexecution.dto.RunCodeRequest;
import com.rohit.leetcode.codeexecution.dto.RunCodeResponse;

public interface RunCodeService {

    RunCodeResponse run(RunCodeRequest request);
}