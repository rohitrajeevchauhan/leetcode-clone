package com.rohit.leetcode.achievement.repository;

import com.rohit.leetcode.achievement.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAchievementRepository
        extends JpaRepository<UserAchievement, Long> {

    boolean existsByUserIdAndAchievementId(
            Long userId,
            Long achievementId
    );

    List<UserAchievement> findByUserIdOrderByEarnedAtDesc(
            Long userId
    );

    long countByUserId(Long userId);
}