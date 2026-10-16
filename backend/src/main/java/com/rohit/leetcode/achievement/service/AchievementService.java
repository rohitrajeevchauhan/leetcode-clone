package com.rohit.leetcode.achievement.service;

import com.rohit.leetcode.achievement.dto.*;

import java.util.List;

public interface AchievementService {

    List<AchievementResponse> getAllAchievements();

    List<UserAchievementResponse> getMyAchievements();

    void checkAchievements(Long userId);

    void awardAchievement(Long userId, String achievementCode);
}