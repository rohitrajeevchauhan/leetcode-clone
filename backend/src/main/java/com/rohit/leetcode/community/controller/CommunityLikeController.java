package com.rohit.leetcode.community.controller;

import com.rohit.leetcode.community.dto.LikeResponse;
import com.rohit.leetcode.community.service.CommunityLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solutions")
@RequiredArgsConstructor
public class CommunityLikeController {

    private final CommunityLikeService likeService;

    @PostMapping("/{solutionId}/like")
    public LikeResponse likeSolution(
            @PathVariable Long solutionId) {

        return likeService.likeSolution(solutionId);
    }

    @DeleteMapping("/{solutionId}/like")
    public LikeResponse unlikeSolution(
            @PathVariable Long solutionId) {

        return likeService.unlikeSolution(solutionId);
    }

    @GetMapping("/{solutionId}/liked")
    public boolean isLiked(
            @PathVariable Long solutionId) {

        return likeService
                .isSolutionLikedByCurrentUser(solutionId);
    }
}