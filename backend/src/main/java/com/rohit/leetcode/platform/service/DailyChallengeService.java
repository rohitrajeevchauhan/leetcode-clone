package com.rohit.leetcode.platform.service;

import com.rohit.leetcode.platform.dto.DailyChallengeResponse;

import java.util.List;

public interface DailyChallengeService {

    DailyChallengeResponse getTodayChallenge();

    DailyChallengeResponse getChallengeByDate(String date);

    DailyChallengeResponse completeChallenge(Long challengeId);

    List<DailyChallengeResponse> getHistory();

    int getCurrentStreak();
}