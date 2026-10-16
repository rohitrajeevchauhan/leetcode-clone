package com.rohit.leetcode.discussion.repository;

import com.rohit.leetcode.discussion.entity.DiscussionLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionLikeRepository
        extends JpaRepository<DiscussionLike, Long> {

    boolean existsByUserIdAndDiscussionId(
            Long userId,
            Long discussionId
    );

    void deleteByUserIdAndDiscussionId(
            Long userId,
            Long discussionId
    );

    long countByDiscussionId(Long discussionId);
}