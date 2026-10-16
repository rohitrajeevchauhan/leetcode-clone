package com.rohit.leetcode.user.service;

import com.rohit.leetcode.user.dto.LeaderboardResponse;

import java.util.List;

public interface LeaderboardService {

    List<LeaderboardResponse> getGlobalLeaderboard();

    List<LeaderboardResponse> getTopUsers(int limit);

    LeaderboardResponse getMyRank();
}