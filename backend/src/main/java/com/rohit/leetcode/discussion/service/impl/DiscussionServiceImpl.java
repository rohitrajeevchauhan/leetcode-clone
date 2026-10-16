package com.rohit.leetcode.discussion.service.impl;

import com.rohit.leetcode.discussion.dto.CommentRequest;
import com.rohit.leetcode.discussion.dto.CommentResponse;
import com.rohit.leetcode.discussion.dto.DiscussionRequest;
import com.rohit.leetcode.discussion.dto.DiscussionResponse;
import com.rohit.leetcode.discussion.entity.Discussion;
import com.rohit.leetcode.discussion.entity.DiscussionComment;
import com.rohit.leetcode.discussion.repository.CommentLikeRepository;
import com.rohit.leetcode.discussion.repository.DiscussionCommentRepository;
import com.rohit.leetcode.discussion.repository.DiscussionLikeRepository;
import com.rohit.leetcode.discussion.repository.DiscussionRepository;
import com.rohit.leetcode.discussion.service.DiscussionService;
import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.problem.repository.ProblemRepository;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscussionServiceImpl implements DiscussionService {

    private final DiscussionRepository discussionRepository;
    private final DiscussionCommentRepository commentRepository;
    private final DiscussionLikeRepository discussionLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    @Override
    public DiscussionResponse createDiscussion(
            DiscussionRequest request) {

        User user = getCurrentUser();

        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() ->
                        new RuntimeException("Problem not found"));

        Discussion discussion = Discussion.builder()
                .problem(problem)
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .likes(0)
                .closed(false)
                .build();

        return mapDiscussion(
                discussionRepository.save(discussion)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscussionResponse> getProblemDiscussions(
            Long problemId) {

        return discussionRepository
                .findByProblemIdOrderByCreatedAtDesc(problemId)
                .stream()
                .map(this::mapDiscussion)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscussionResponse> getTopDiscussions(
            Long problemId) {

        return discussionRepository
                .findByProblemIdOrderByLikesDesc(problemId)
                .stream()
                .map(this::mapDiscussion)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscussionResponse> getMyDiscussions() {

        User user = getCurrentUser();

        return discussionRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapDiscussion)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DiscussionResponse getDiscussion(
            Long discussionId) {

        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() ->
                        new RuntimeException("Discussion not found"));

        return mapDiscussion(discussion);
    }

    @Override
    public CommentResponse addComment(
            CommentRequest request) {

        User user = getCurrentUser();

        Discussion discussion =
                discussionRepository.findById(
                        request.getDiscussionId()
                ).orElseThrow(() ->
                        new RuntimeException("Discussion not found"));

        if (Boolean.TRUE.equals(discussion.getClosed())) {
            throw new RuntimeException(
                    "This discussion is closed"
            );
        }

        DiscussionComment comment = DiscussionComment.builder()
                .discussion(discussion)
                .user(user)
                .content(request.getContent())
                .likes(0)
                .build();

        return mapComment(
                commentRepository.save(comment)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(
            Long discussionId) {

        return commentRepository
                .findByDiscussionIdOrderByCreatedAtAsc(discussionId)
                .stream()
                .map(this::mapComment)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getTopComments(
            Long discussionId) {

        return commentRepository
                .findByDiscussionIdOrderByLikesDesc(discussionId)
                .stream()
                .map(this::mapComment)
                .toList();
    }

    @Override
    public DiscussionResponse updateDiscussion(
            Long discussionId,
            DiscussionRequest request) {

        User currentUser = getCurrentUser();

        Discussion discussion =
                discussionRepository.findById(discussionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Discussion not found"));

        checkOwnership(
                discussion.getUser(),
                currentUser,
                "discussion"
        );

        discussion.setTitle(request.getTitle());
        discussion.setContent(request.getContent());

        return mapDiscussion(
                discussionRepository.save(discussion)
        );
    }

    @Override
    public CommentResponse updateComment(
            Long commentId,
            CommentRequest request) {

        User currentUser = getCurrentUser();

        DiscussionComment comment =
                commentRepository.findById(commentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Comment not found"));

        checkOwnership(
                comment.getUser(),
                currentUser,
                "comment"
        );

        comment.setContent(request.getContent());

        return mapComment(
                commentRepository.save(comment)
        );
    }

    // =========================================================
    // CLOSE DISCUSSION
    // =========================================================

    @Override
    public void closeDiscussion(Long discussionId) {

        User currentUser = getCurrentUser();

        Discussion discussion =
                discussionRepository.findById(discussionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Discussion not found"));

        checkOwnership(
                discussion.getUser(),
                currentUser,
                "discussion"
        );

        if (Boolean.TRUE.equals(discussion.getClosed())) {
            throw new RuntimeException(
                    "Discussion is already closed"
            );
        }

        discussion.setClosed(true);

        discussionRepository.save(discussion);
    }

    // =========================================================
    // REOPEN DISCUSSION
    // =========================================================

    @Override
    public void reopenDiscussion(Long discussionId) {

        User currentUser = getCurrentUser();

        Discussion discussion =
                discussionRepository.findById(discussionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Discussion not found"));

        checkOwnership(
                discussion.getUser(),
                currentUser,
                "discussion"
        );

        if (Boolean.FALSE.equals(discussion.getClosed())) {
            throw new RuntimeException(
                    "Discussion is already open"
            );
        }

        discussion.setClosed(false);

        discussionRepository.save(discussion);
    }

    @Override
    public void deleteDiscussion(Long discussionId) {

        User currentUser = getCurrentUser();

        Discussion discussion =
                discussionRepository.findById(discussionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Discussion not found"));

        checkOwnership(
                discussion.getUser(),
                currentUser,
                "discussion"
        );

        discussionRepository.delete(discussion);
    }

    @Override
    public void deleteComment(Long commentId) {

        User currentUser = getCurrentUser();

        DiscussionComment comment =
                commentRepository.findById(commentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Comment not found"));

        checkOwnership(
                comment.getUser(),
                currentUser,
                "comment"
        );

        commentRepository.delete(comment);
    }

    private void checkOwnership(
            User owner,
            User currentUser,
            String resource) {

        if (!owner.getId().equals(currentUser.getId())) {
            throw new RuntimeException(
                    "You can only modify your own " + resource
            );
        }
    }

    private DiscussionResponse mapDiscussion(
            Discussion discussion) {

        User currentUser = getCurrentUserIfAvailable();

        boolean liked = false;

        if (currentUser != null) {
            liked =
                    discussionLikeRepository
                            .existsByUserIdAndDiscussionId(
                                    currentUser.getId(),
                                    discussion.getId()
                            );
        }

        int commentCount =
                (int) commentRepository
                        .countByDiscussionId(
                                discussion.getId()
                        );

        return DiscussionResponse.builder()
                .id(discussion.getId())
                .problemId(discussion.getProblem().getId())
                .problemTitle(discussion.getProblem().getTitle())
                .userId(discussion.getUser().getId())
                .username(discussion.getUser().getUsername())
                .title(discussion.getTitle())
                .content(discussion.getContent())
                .likes(discussion.getLikes())
                .likedByCurrentUser(liked)
                .commentCount(commentCount)
                .closed(discussion.getClosed())
                .createdAt(discussion.getCreatedAt())
                .updatedAt(discussion.getUpdatedAt())
                .build();
    }

    private CommentResponse mapComment(
            DiscussionComment comment) {

        User currentUser = getCurrentUserIfAvailable();

        boolean liked = false;

        if (currentUser != null) {
            liked =
                    commentLikeRepository
                            .existsByUserIdAndCommentId(
                                    currentUser.getId(),
                                    comment.getId()
                            );
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .discussionId(comment.getDiscussion().getId())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .content(comment.getContent())
                .likes(comment.getLikes())
                .likedByCurrentUser(liked)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(
                        authentication.getPrincipal())) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Current user not found"
                        )
                );
    }

    private User getCurrentUserIfAvailable() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(
                        authentication.getPrincipal())) {

            return null;
        }

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElse(null);
    }
}