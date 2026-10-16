package com.rohit.leetcode.platform.gamification.controller;

import com.rohit.leetcode.platform.gamification.dto.GamificationDashboardResponse;
import com.rohit.leetcode.platform.gamification.dto.LeaderboardEntryResponse;
import com.rohit.leetcode.platform.gamification.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/platform/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService gamificationService;

    @GetMapping("/dashboard")
    public GamificationDashboardResponse getDashboard() {
        return gamificationService.getDashboard();
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardEntryResponse> getLeaderboard() {
        return gamificationService.getLeaderboard();
    }
}