package com.rohit.leetcode.discussion.controller;

import com.rohit.leetcode.discussion.service.DiscussionLikeService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discussions")
@RequiredArgsConstructor
public class DiscussionLikeController {

    private final DiscussionLikeService likeService;


    // =========================================================
    // LIKE DISCUSSION
    // =========================================================

    @PostMapping("/{discussionId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void likeDiscussion(
            @PathVariable Long discussionId) {

        likeService.likeDiscussion(discussionId);
    }


    // =========================================================
    // UNLIKE DISCUSSION
    // =========================================================

    @DeleteMapping("/{discussionId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlikeDiscussion(
            @PathVariable Long discussionId) {

        likeService.unlikeDiscussion(discussionId);
    }


    // =========================================================
    // LIKE COMMENT
    // =========================================================

    @PostMapping("/comments/{commentId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void likeComment(
            @PathVariable Long commentId) {

        likeService.likeComment(commentId);
    }


    // =========================================================
    // UNLIKE COMMENT
    // =========================================================

    @DeleteMapping("/comments/{commentId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlikeComment(
            @PathVariable Long commentId) {

        likeService.unlikeComment(commentId);
    }
}