package com.rohit.leetcode.discussion.service;

import com.rohit.leetcode.discussion.dto.CommentRequest;
import com.rohit.leetcode.discussion.dto.CommentResponse;
import com.rohit.leetcode.discussion.dto.DiscussionRequest;
import com.rohit.leetcode.discussion.dto.DiscussionResponse;

import java.util.List;

public interface DiscussionService {

    DiscussionResponse createDiscussion(
            DiscussionRequest request
    );

    List<DiscussionResponse> getProblemDiscussions(
            Long problemId
    );

    List<DiscussionResponse> getTopDiscussions(
            Long problemId
    );

    List<DiscussionResponse> getMyDiscussions();

    DiscussionResponse getDiscussion(
            Long discussionId
    );

    CommentResponse addComment(
            CommentRequest request
    );

    List<CommentResponse> getComments(
            Long discussionId
    );

    List<CommentResponse> getTopComments(
            Long discussionId
    );

    DiscussionResponse updateDiscussion(
            Long discussionId,
            DiscussionRequest request
    );

    CommentResponse updateComment(
            Long commentId,
            CommentRequest request
    );

    void closeDiscussion(
            Long discussionId
    );

    void reopenDiscussion(
            Long discussionId
    );

    void deleteDiscussion(
            Long discussionId
    );

    void deleteComment(
            Long commentId
    );
}