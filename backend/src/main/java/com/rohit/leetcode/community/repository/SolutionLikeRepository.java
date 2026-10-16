package com.rohit.leetcode.community.repository;

import com.rohit.leetcode.community.entity.SolutionLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionLikeRepository
        extends JpaRepository<SolutionLike, Long> {

    boolean existsBySolutionIdAndUserId(
            Long solutionId,
            Long userId
    );

    void deleteBySolutionIdAndUserId(
            Long solutionId,
            Long userId
    );

    long countBySolutionId(Long solutionId);
}