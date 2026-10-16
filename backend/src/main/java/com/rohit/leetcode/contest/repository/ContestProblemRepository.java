package com.rohit.leetcode.contest.repository;

import com.rohit.leetcode.contest.entity.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContestProblemRepository
        extends JpaRepository<ContestProblem, Long> {

    List<ContestProblem> findByContestIdOrderByProblemOrderAsc(
            Long contestId
    );

    Optional<ContestProblem> findByContestIdAndProblemId(
            Long contestId,
            Long problemId
    );

    boolean existsByContestIdAndProblemId(
            Long contestId,
            Long problemId
    );

    long countByContestId(Long contestId);
}