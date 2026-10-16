package com.rohit.leetcode.discussion.repository;

import com.rohit.leetcode.discussion.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository
        extends JpaRepository<CommentLike, Long> {

    boolean existsByUserIdAndCommentId(
            Long userId,
            Long commentId
    );

    void deleteByUserIdAndCommentId(
            Long userId,
            Long commentId
    );

    long countByCommentId(Long commentId);
}