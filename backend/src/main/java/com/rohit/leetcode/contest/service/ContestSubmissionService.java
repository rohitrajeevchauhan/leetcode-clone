package com.rohit.leetcode.contest.service;

import com.rohit.leetcode.contest.dto.*;

import java.util.List;

public interface ContestSubmissionService {

    ContestSubmissionResponse submit(
            ContestSubmissionRequest request
    );

    List<ContestSubmissionResponse> getMySubmissions(
            Long contestId
    );

    List<ContestLeaderboardResponse> getLeaderboard(
            Long contestId
    );
}