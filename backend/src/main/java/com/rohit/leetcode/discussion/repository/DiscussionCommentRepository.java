package com.rohit.leetcode.discussion.repository;

import com.rohit.leetcode.discussion.entity.DiscussionComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscussionCommentRepository
        extends JpaRepository<DiscussionComment, Long> {

    List<DiscussionComment> findByDiscussionIdOrderByCreatedAtAsc(
            Long discussionId
    );

    List<DiscussionComment> findByDiscussionIdOrderByLikesDesc(
            Long discussionId
    );

    long countByDiscussionId(Long discussionId);
}