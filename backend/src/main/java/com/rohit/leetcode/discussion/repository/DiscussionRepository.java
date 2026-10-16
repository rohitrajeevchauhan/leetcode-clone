package com.rohit.leetcode.discussion.repository;

import com.rohit.leetcode.discussion.entity.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscussionRepository
        extends JpaRepository<Discussion, Long> {

    List<Discussion> findByProblemIdOrderByCreatedAtDesc(
            Long problemId
    );

    List<Discussion> findByProblemIdOrderByLikesDesc(
            Long problemId
    );

    List<Discussion> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    List<Discussion> findTop10ByOrderByLikesDesc();
}