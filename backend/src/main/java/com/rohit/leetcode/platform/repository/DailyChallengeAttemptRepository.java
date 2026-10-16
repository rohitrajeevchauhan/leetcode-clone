package com.rohit.leetcode.platform.repository;

import com.rohit.leetcode.platform.entity.DailyChallengeAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DailyChallengeAttemptRepository
        extends JpaRepository<DailyChallengeAttempt, Long> {

    Optional<DailyChallengeAttempt>
    findByChallengeIdAndUserId(
            Long challengeId,
            Long userId
    );

    List<DailyChallengeAttempt>
    findByUserIdOrderByCreatedAtDesc(Long userId);

    List<DailyChallengeAttempt>
    findByUserIdAndCompletedTrueOrderByCompletedAtDesc(
            Long userId
    );

    boolean existsByChallengeIdAndUserId(
            Long challengeId,
            Long userId
    );

    boolean existsByChallengeIdAndUserIdAndCompletedTrue(
            Long challengeId,
            Long userId
    );

    long countByUserIdAndCompletedTrue(Long userId);
}