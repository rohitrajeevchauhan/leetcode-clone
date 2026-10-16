package com.rohit.leetcode.community.repository;

import com.rohit.leetcode.community.entity.CommunitySolution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunitySolutionRepository
        extends JpaRepository<CommunitySolution, Long> {

    List<CommunitySolution>
    findByProblemIdAndPublishedTrueOrderByCreatedAtDesc(
            Long problemId
    );

    List<CommunitySolution>
    findByProblemIdAndPublishedTrueOrderByLikesDesc(
            Long problemId
    );

    List<CommunitySolution>
    findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    long countByProblemId(Long problemId);
}