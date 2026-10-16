package com.rohit.leetcode.community.service.impl;

import com.rohit.leetcode.community.dto.CommunitySolutionRequest;
import com.rohit.leetcode.community.dto.CommunitySolutionResponse;
import com.rohit.leetcode.community.entity.CommunitySolution;
import com.rohit.leetcode.community.repository.CommunitySolutionRepository;
import com.rohit.leetcode.community.repository.SolutionLikeRepository;
import com.rohit.leetcode.community.service.CommunitySolutionService;
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
public class CommunitySolutionServiceImpl
        implements CommunitySolutionService {

    private final CommunitySolutionRepository solutionRepository;

    private final ProblemRepository problemRepository;

    private final UserRepository userRepository;

    private final SolutionLikeRepository likeRepository;


    @Override
    public CommunitySolutionResponse createSolution(
            CommunitySolutionRequest request) {

        User currentUser = getCurrentUser();

        Problem problem =
                problemRepository.findById(
                                request.getProblemId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Problem not found"
                                ));

        CommunitySolution solution =
                CommunitySolution.builder()
                        .problem(problem)
                        .user(currentUser)
                        .title(request.getTitle())
                        .language(request.getLanguage())
                        .code(request.getCode())
                        .explanation(request.getExplanation())
                        .timeComplexity(
                                request.getTimeComplexity()
                        )
                        .spaceComplexity(
                                request.getSpaceComplexity()
                        )
                        .likes(0)
                        .published(true)
                        .build();

        return mapToResponse(
                solutionRepository.save(solution)
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<CommunitySolutionResponse> getProblemSolutions(
            Long problemId) {

        return solutionRepository
                .findByProblemIdAndPublishedTrueOrderByCreatedAtDesc(
                        problemId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<CommunitySolutionResponse> getTopSolutions(
            Long problemId) {

        return solutionRepository
                .findByProblemIdAndPublishedTrueOrderByLikesDesc(
                        problemId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<CommunitySolutionResponse> getMySolutions() {

        User currentUser = getCurrentUser();

        return solutionRepository
                .findByUserIdOrderByCreatedAtDesc(
                        currentUser.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public CommunitySolutionResponse getSolution(
            Long solutionId) {

        CommunitySolution solution =
                solutionRepository.findById(solutionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Community solution not found"
                                ));

        return mapToResponse(solution);
    }


    @Override
    public void deleteSolution(Long solutionId) {

        User currentUser = getCurrentUser();

        CommunitySolution solution =
                solutionRepository.findById(solutionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Community solution not found"
                                ));

        boolean isOwner =
                solution.getUser()
                        .getId()
                        .equals(currentUser.getId());

        boolean isAdmin =
                currentUser.getRole() == User.Role.ADMIN;

        if (!isOwner && !isAdmin) {

            throw new RuntimeException(
                    "You can only delete your own solution"
            );
        }

        solutionRepository.delete(solution);
    }


    private CommunitySolutionResponse mapToResponse(
            CommunitySolution solution) {

        boolean likedByCurrentUser = false;

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication != null &&
                authentication.isAuthenticated() &&
                authentication.getName() != null) {

            String email = authentication.getName();

            User currentUser =
                    userRepository.findByEmail(email)
                            .orElse(null);

            if (currentUser != null) {

                likedByCurrentUser =
                        likeRepository
                                .existsBySolutionIdAndUserId(
                                        solution.getId(),
                                        currentUser.getId()
                                );
            }
        }

        return CommunitySolutionResponse.builder()
                .id(solution.getId())
                .problemId(
                        solution.getProblem().getId()
                )
                .problemTitle(
                        solution.getProblem().getTitle()
                )
                .userId(
                        solution.getUser().getId()
                )
                .username(
                        solution.getUser().getUsername()
                )
                .title(solution.getTitle())
                .language(solution.getLanguage())
                .code(solution.getCode())
                .explanation(solution.getExplanation())
                .timeComplexity(
                        solution.getTimeComplexity()
                )
                .spaceComplexity(
                        solution.getSpaceComplexity()
                )
                .likes(solution.getLikes())
                .likedByCurrentUser(
                        likedByCurrentUser
                )
                .createdAt(solution.getCreatedAt())
                .updatedAt(solution.getUpdatedAt())
                .build();
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