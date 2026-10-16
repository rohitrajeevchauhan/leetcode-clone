package com.rohit.leetcode.contest.service;

import com.rohit.leetcode.contest.dto.*;

import java.util.List;

public interface ContestService {

    ContestResponse createContest(
            CreateContestRequest request
    );

    ContestResponse getContest(Long contestId);

    List<ContestResponse> getAllContests();

    void addProblem(
            Long contestId,
            Long problemId,
            Integer order,
            Integer points
    );

    List<ContestProblemResponse> getContestProblems(
            Long contestId
    );

    void register(Long contestId);

    boolean isRegistered(Long contestId);
}