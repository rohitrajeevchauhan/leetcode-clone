package com.rohit.leetcode.discussion.service.impl;

import com.rohit.leetcode.discussion.entity.CommentLike;
import com.rohit.leetcode.discussion.entity.Discussion;
import com.rohit.leetcode.discussion.entity.DiscussionComment;
import com.rohit.leetcode.discussion.entity.DiscussionLike;
import com.rohit.leetcode.discussion.repository.CommentLikeRepository;
import com.rohit.leetcode.discussion.repository.DiscussionCommentRepository;
import com.rohit.leetcode.discussion.repository.DiscussionLikeRepository;
import com.rohit.leetcode.discussion.repository.DiscussionRepository;
import com.rohit.leetcode.discussion.service.DiscussionLikeService;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscussionLikeServiceImpl
        implements DiscussionLikeService {

    private final DiscussionRepository discussionRepository;
    private final DiscussionLikeRepository discussionLikeRepository;

    private final DiscussionCommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    private final UserRepository userRepository;


    // =========================================================
    // LIKE DISCUSSION
    // =========================================================

    @Override
    public void likeDiscussion(Long discussionId) {

        User user = getCurrentUser();

        Discussion discussion =
                discussionRepository
                        .findById(discussionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Discussion not found"));

        boolean alreadyLiked =
                discussionLikeRepository
                        .existsByUserIdAndDiscussionId(
                                user.getId(),
                                discussionId
                        );

        if (alreadyLiked) {
            return;
        }

        DiscussionLike like =
                DiscussionLike.builder()
                        .discussion(discussion)
                        .user(user)
                        .build();

        discussionLikeRepository.save(like);

        discussion.setLikes(
                discussion.getLikes() + 1
        );

        discussionRepository.save(discussion);
    }


    // =========================================================
    // UNLIKE DISCUSSION
    // =========================================================

    @Override
    public void unlikeDiscussion(Long discussionId) {

        User user = getCurrentUser();

        Discussion discussion =
                discussionRepository
                        .findById(discussionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Discussion not found"));

        boolean alreadyLiked =
                discussionLikeRepository
                        .existsByUserIdAndDiscussionId(
                                user.getId(),
                                discussionId
                        );

        if (!alreadyLiked) {
            return;
        }

        discussionLikeRepository
                .deleteByUserIdAndDiscussionId(
                        user.getId(),
                        discussionId
                );

        int currentLikes = discussion.getLikes();

        discussion.setLikes(
                Math.max(0, currentLikes - 1)
        );

        discussionRepository.save(discussion);
    }


    // =========================================================
    // LIKE COMMENT
    // =========================================================

    @Override
    public void likeComment(Long commentId) {

        User user = getCurrentUser();

        DiscussionComment comment =
                commentRepository
                        .findById(commentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Comment not found"));

        boolean alreadyLiked =
                commentLikeRepository
                        .existsByUserIdAndCommentId(
                                user.getId(),
                                commentId
                        );

        if (alreadyLiked) {
            return;
        }

        CommentLike like =
                CommentLike.builder()
                        .comment(comment)
                        .user(user)
                        .build();

        commentLikeRepository.save(like);

        comment.setLikes(
                comment.getLikes() + 1
        );

        commentRepository.save(comment);
    }


    // =========================================================
    // UNLIKE COMMENT
    // =========================================================

    @Override
    public void unlikeComment(Long commentId) {

        User user = getCurrentUser();

        DiscussionComment comment =
                commentRepository
                        .findById(commentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Comment not found"));

        boolean alreadyLiked =
                commentLikeRepository
                        .existsByUserIdAndCommentId(
                                user.getId(),
                                commentId
                        );

        if (!alreadyLiked) {
            return;
        }

        commentLikeRepository
                .deleteByUserIdAndCommentId(
                        user.getId(),
                        commentId
                );

        int currentLikes = comment.getLikes();

        comment.setLikes(
                Math.max(0, currentLikes - 1)
        );

        commentRepository.save(comment);
    }


    // =========================================================
    // CURRENT USER
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(
                        authentication.getPrincipal())) {

            throw new SecurityException(
                    "User is not authenticated"
            );
        }

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new SecurityException(
                                "Authenticated user not found"
                        )
                );
    }
}