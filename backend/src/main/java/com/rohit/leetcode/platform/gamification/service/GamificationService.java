package com.rohit.leetcode.platform.gamification.service;

import com.rohit.leetcode.platform.gamification.dto.GamificationDashboardResponse;
import com.rohit.leetcode.platform.gamification.dto.LeaderboardEntryResponse;
import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.user.entity.User;

import java.util.List;

public interface GamificationService {

    GamificationDashboardResponse getDashboard();

    List<LeaderboardEntryResponse> getLeaderboard();

    void awardAcceptedSubmission(User user, Problem problem);
}