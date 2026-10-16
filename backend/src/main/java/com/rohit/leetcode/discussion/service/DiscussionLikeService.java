package com.rohit.leetcode.discussion.service;

public interface DiscussionLikeService {

    void likeDiscussion(Long discussionId);

    void unlikeDiscussion(Long discussionId);

    void likeComment(Long commentId);

    void unlikeComment(Long commentId);
}