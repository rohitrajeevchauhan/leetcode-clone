package com.rohit.leetcode.problem.service;

import com.rohit.leetcode.problem.dto.ProblemRequest;
import com.rohit.leetcode.problem.dto.ProblemResponse;

import java.util.List;

public interface ProblemService {

    ProblemResponse createProblem(
            ProblemRequest request
    );

    List<ProblemResponse> getAllProblems();

    ProblemResponse getProblemById(
            Long id
    );

    ProblemResponse updateProblem(
            Long id,
            ProblemRequest request
    );

    void deleteProblem(
            Long id
    );
}