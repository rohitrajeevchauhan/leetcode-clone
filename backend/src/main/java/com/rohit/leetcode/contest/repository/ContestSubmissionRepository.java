package com.rohit.leetcode.contest.repository;

import com.rohit.leetcode.contest.entity.ContestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestSubmissionRepository
        extends JpaRepository<ContestSubmission, Long> {

    List<ContestSubmission> findByContestIdAndUserIdOrderByCreatedAtAsc(
            Long contestId,
            Long userId
    );

    List<ContestSubmission> findByContestIdAndUserIdAndProblemIdOrderByCreatedAtAsc(
            Long contestId,
            Long userId,
            Long problemId
    );

    List<ContestSubmission> findByContestIdAndUserIdAndProblemIdAndStatus(
            Long contestId,
            Long userId,
            Long problemId,
            ContestSubmission.SubmissionStatus status
    );

    List<ContestSubmission> findByContestIdOrderByCreatedAtAsc(
            Long contestId
    );

    long countByContestIdAndUserId(
            Long contestId,
            Long userId
    );

    long countByContestIdAndUserIdAndProblemId(
            Long contestId,
            Long userId,
            Long problemId
    );
}