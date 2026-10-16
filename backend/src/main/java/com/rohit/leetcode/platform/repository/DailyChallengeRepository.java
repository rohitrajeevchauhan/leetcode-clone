package com.rohit.leetcode.platform.repository;

import com.rohit.leetcode.platform.entity.DailyChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyChallengeRepository
        extends JpaRepository<DailyChallenge, Long> {

    Optional<DailyChallenge>
    findByChallengeDate(LocalDate challengeDate);

    Optional<DailyChallenge>
    findByChallengeDateAndActiveTrue(LocalDate challengeDate);

    boolean existsByChallengeDate(LocalDate challengeDate);

    boolean existsByChallengeDateAndProblemId(
            LocalDate challengeDate,
            Long problemId
    );
}