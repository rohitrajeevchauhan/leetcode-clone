package com.rohit.leetcode.user.controller;

import com.rohit.leetcode.user.dto.LeaderboardResponse;
import com.rohit.leetcode.user.service.LeaderboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;


    // =========================================================
    // GLOBAL LEADERBOARD
    // =========================================================

    @GetMapping
    public List<LeaderboardResponse> getGlobalLeaderboard() {

        return leaderboardService
                .getGlobalLeaderboard();
    }


    // =========================================================
    // TOP USERS
    // =========================================================

    @GetMapping("/top")
    public List<LeaderboardResponse> getTopUsers(
            @RequestParam(defaultValue = "10")
            int limit) {

        return leaderboardService
                .getTopUsers(limit);
    }


    // =========================================================
    // MY RANK
    // =========================================================

    @GetMapping("/me")
    public LeaderboardResponse getMyRank() {

        return leaderboardService
                .getMyRank();
    }
}