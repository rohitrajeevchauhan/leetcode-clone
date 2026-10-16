package com.rohit.leetcode.community.service;

import com.rohit.leetcode.community.dto.LikeResponse;

public interface CommunityLikeService {

    LikeResponse likeSolution(Long solutionId);

    LikeResponse unlikeSolution(Long solutionId);

    boolean isSolutionLikedByCurrentUser(Long solutionId);
}