package com.rohit.leetcode.problem.service;

import com.rohit.leetcode.problem.dto.ProblemProgressResponse;

import java.util.List;

public interface ProblemProgressService {

    List<ProblemProgressResponse> getMyProblemProgress();

    ProblemProgressResponse getMyProblemProgress(Long problemId);
}