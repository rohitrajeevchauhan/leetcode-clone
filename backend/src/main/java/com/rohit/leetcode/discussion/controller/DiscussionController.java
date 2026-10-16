package com.rohit.leetcode.discussion.controller;

import com.rohit.leetcode.discussion.dto.CommentRequest;
import com.rohit.leetcode.discussion.dto.CommentResponse;
import com.rohit.leetcode.discussion.dto.DiscussionRequest;
import com.rohit.leetcode.discussion.dto.DiscussionResponse;
import com.rohit.leetcode.discussion.service.DiscussionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discussions")
@RequiredArgsConstructor
public class DiscussionController {

    private final DiscussionService discussionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiscussionResponse createDiscussion(
            @Valid @RequestBody DiscussionRequest request) {

        return discussionService.createDiscussion(request);
    }

    @GetMapping("/problem/{problemId}")
    public List<DiscussionResponse> getProblemDiscussions(
            @PathVariable Long problemId) {

        return discussionService
                .getProblemDiscussions(problemId);
    }

    @GetMapping("/problem/{problemId}/top")
    public List<DiscussionResponse> getTopDiscussions(
            @PathVariable Long problemId) {

        return discussionService
                .getTopDiscussions(problemId);
    }

    @GetMapping("/my")
    public List<DiscussionResponse> getMyDiscussions() {

        return discussionService.getMyDiscussions();
    }

    @GetMapping("/{discussionId}")
    public DiscussionResponse getDiscussion(
            @PathVariable Long discussionId) {

        return discussionService
                .getDiscussion(discussionId);
    }

    // =========================================================
    // UPDATE DISCUSSION
    // =========================================================

    @PutMapping("/{discussionId}")
    public DiscussionResponse updateDiscussion(
            @PathVariable Long discussionId,
            @Valid @RequestBody DiscussionRequest request) {

        return discussionService.updateDiscussion(
                discussionId,
                request
        );
    }

    // =========================================================
    // CLOSE DISCUSSION
    // =========================================================

    @PutMapping("/{discussionId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeDiscussion(
            @PathVariable Long discussionId) {

        discussionService.closeDiscussion(
                discussionId
        );
    }

    // =========================================================
    // REOPEN DISCUSSION
    // =========================================================

    @PutMapping("/{discussionId}/reopen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reopenDiscussion(
            @PathVariable Long discussionId) {

        discussionService.reopenDiscussion(
                discussionId
        );
    }

    // =========================================================
    // ADD COMMENT
    // =========================================================

    @PostMapping("/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(
            @Valid @RequestBody CommentRequest request) {

        return discussionService.addComment(request);
    }

    // =========================================================
    // GET COMMENTS
    // =========================================================

    @GetMapping("/{discussionId}/comments")
    public List<CommentResponse> getComments(
            @PathVariable Long discussionId) {

        return discussionService.getComments(
                discussionId
        );
    }

    @GetMapping("/{discussionId}/comments/top")
    public List<CommentResponse> getTopComments(
            @PathVariable Long discussionId) {

        return discussionService.getTopComments(
                discussionId
        );
    }

    // =========================================================
    // UPDATE COMMENT
    // =========================================================

    @PutMapping("/comments/{commentId}")
    public CommentResponse updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request) {

        return discussionService.updateComment(
                commentId,
                request
        );
    }

    // =========================================================
    // DELETE DISCUSSION
    // =========================================================

    @DeleteMapping("/{discussionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDiscussion(
            @PathVariable Long discussionId) {

        discussionService.deleteDiscussion(
                discussionId
        );
    }

    // =========================================================
    // DELETE COMMENT
    // =========================================================

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long commentId) {

        discussionService.deleteComment(
                commentId
        );
    }
}