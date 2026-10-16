package com.rohit.leetcode.platform.gamification.repository;

import com.rohit.leetcode.platform.gamification.entity.SolvedProblemReward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolvedProblemRewardRepository
        extends JpaRepository<SolvedProblemReward, Long> {

    boolean existsByUserIdAndProblemId(Long userId, Long problemId);

    long countByUserId(Long userId);
}