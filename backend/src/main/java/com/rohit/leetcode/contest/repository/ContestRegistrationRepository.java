package com.rohit.leetcode.contest.repository;

import com.rohit.leetcode.contest.entity.ContestRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestRegistrationRepository
        extends JpaRepository<ContestRegistration, Long> {

    boolean existsByContestIdAndUserId(
            Long contestId,
            Long userId
    );

    long countByContestId(Long contestId);

    List<ContestRegistration> findByContestId(
            Long contestId
    );

    List<ContestRegistration> findByUserIdOrderByRegisteredAtDesc(
            Long userId
    );
}