package com.rohit.leetcode.platform.gamification.repository;

import com.rohit.leetcode.platform.gamification.entity.GamificationAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GamificationAchievementRepository
        extends JpaRepository<GamificationAchievement, Long> {

    boolean existsByUserIdAndAchievementCode(
            Long userId,
            String achievementCode
    );

    List<GamificationAchievement> findByUserIdOrderByUnlockedAtDesc(
            Long userId
    );

    long countByUserId(Long userId);
}