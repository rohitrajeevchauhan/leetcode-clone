package com.rohit.leetcode.community.service.impl;

import com.rohit.leetcode.community.dto.LikeResponse;
import com.rohit.leetcode.community.entity.CommunitySolution;
import com.rohit.leetcode.community.entity.SolutionLike;
import com.rohit.leetcode.community.repository.CommunitySolutionRepository;
import com.rohit.leetcode.community.repository.SolutionLikeRepository;
import com.rohit.leetcode.community.service.CommunityLikeService;
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
public class CommunityLikeServiceImpl
        implements CommunityLikeService {

    private final SolutionLikeRepository likeRepository;

    private final CommunitySolutionRepository solutionRepository;

    private final UserRepository userRepository;

    @Override
    public LikeResponse likeSolution(Long solutionId) {

        User currentUser = getCurrentUser();

        CommunitySolution solution =
                solutionRepository.findById(solutionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Community solution not found"
                                ));

        boolean alreadyLiked =
                likeRepository.existsBySolutionIdAndUserId(
                        solutionId,
                        currentUser.getId()
                );

        if (!alreadyLiked) {

            SolutionLike like = SolutionLike.builder()
                    .solution(solution)
                    .user(currentUser)
                    .build();

            likeRepository.save(like);

            solution.setLikes(
                    solution.getLikes() + 1
            );

            solutionRepository.save(solution);
        }

        return LikeResponse.builder()
                .targetId(solutionId)
                .liked(true)
                .likes(solution.getLikes())
                .build();
    }

    @Override
    public LikeResponse unlikeSolution(Long solutionId) {

        User currentUser = getCurrentUser();

        CommunitySolution solution =
                solutionRepository.findById(solutionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Community solution not found"
                                ));

        boolean alreadyLiked =
                likeRepository.existsBySolutionIdAndUserId(
                        solutionId,
                        currentUser.getId()
                );

        if (alreadyLiked) {

            likeRepository.deleteBySolutionIdAndUserId(
                    solutionId,
                    currentUser.getId()
            );

            solution.setLikes(
                    Math.max(0, solution.getLikes() - 1)
            );

            solutionRepository.save(solution);
        }

        return LikeResponse.builder()
                .targetId(solutionId)
                .liked(false)
                .likes(solution.getLikes())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSolutionLikedByCurrentUser(
            Long solutionId) {

        User currentUser = getCurrentUser();

        return likeRepository.existsBySolutionIdAndUserId(
                solutionId,
                currentUser.getId()
        );
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

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
                        ));
    }
}