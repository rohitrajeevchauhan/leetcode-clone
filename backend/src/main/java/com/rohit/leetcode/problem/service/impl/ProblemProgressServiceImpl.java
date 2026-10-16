package com.rohit.leetcode.problem.service.impl;

import com.rohit.leetcode.problem.dto.ProblemProgressResponse;
import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.problem.repository.ProblemRepository;
import com.rohit.leetcode.problem.service.ProblemProgressService;
import com.rohit.leetcode.submission.entity.Submission.SubmissionStatus;
import com.rohit.leetcode.submission.repository.SubmissionRepository;
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
@Transactional(readOnly = true)
public class ProblemProgressServiceImpl
        implements ProblemProgressService {

    private final ProblemRepository problemRepository;

    private final SubmissionRepository submissionRepository;

    private final UserRepository userRepository;


    // =========================================================
    // ALL PROBLEM PROGRESS
    // =========================================================

    @Override
    public List<ProblemProgressResponse> getMyProblemProgress() {

        User user = getCurrentUser();

        return problemRepository
                .findAll()
                .stream()
                .map(problem ->
                        buildProgress(
                                user,
                                problem
                        )
                )
                .toList();
    }


    // =========================================================
    // SINGLE PROBLEM PROGRESS
    // =========================================================

    @Override
    public ProblemProgressResponse getMyProblemProgress(
            Long problemId) {

        User user = getCurrentUser();

        Problem problem =
                problemRepository
                        .findById(problemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Problem not found"
                                )
                        );

        return buildProgress(
                user,
                problem
        );
    }


    // =========================================================
    // BUILD PROGRESS
    // =========================================================

    private ProblemProgressResponse buildProgress(
            User user,
            Problem problem) {

        Long userId = user.getId();

        Long problemId = problem.getId();


        // -----------------------------------------------------
        // TOTAL ATTEMPTS
        // -----------------------------------------------------

        long totalAttempts =
                submissionRepository
                        .countByUserIdAndProblemId(
                                userId,
                                problemId
                        );


        // -----------------------------------------------------
        // ACCEPTED ATTEMPTS
        // -----------------------------------------------------

        long acceptedAttempts =
                submissionRepository
                        .countByUserIdAndProblemIdAndStatus(
                                userId,
                                problemId,
                                SubmissionStatus.ACCEPTED
                        );


        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        String status;

        if (acceptedAttempts > 0) {

            status = "SOLVED";

        } else if (totalAttempts > 0) {

            status = "ATTEMPTED";

        } else {

            status = "NOT_STARTED";
        }


        return ProblemProgressResponse.builder()

                .problemId(
                        problem.getId()
                )

                .title(
                        problem.getTitle()
                )

                .difficulty(
                        problem.getDifficulty()
                )

                .topic(
                        problem.getTopic()
                )

                .tags(
                        problem.getTags()
                )

                .status(
                        status
                )

                .totalAttempts(
                        totalAttempts
                )

                .acceptedAttempts(
                        acceptedAttempts
                )

                .build();
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
                        authentication.getPrincipal()
                )) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Current user not found"
                        )
                );
    }
}