package com.rohit.leetcode.community.service;

import com.rohit.leetcode.community.dto.CommunitySolutionRequest;
import com.rohit.leetcode.community.dto.CommunitySolutionResponse;

import java.util.List;

public interface CommunitySolutionService {

    CommunitySolutionResponse createSolution(
            CommunitySolutionRequest request
    );

    List<CommunitySolutionResponse> getProblemSolutions(
            Long problemId
    );

    List<CommunitySolutionResponse> getTopSolutions(
            Long problemId
    );

    List<CommunitySolutionResponse> getMySolutions();

    CommunitySolutionResponse getSolution(
            Long solutionId
    );

    void deleteSolution(Long solutionId);
}