package com.rohit.leetcode.user.service.impl;

import com.rohit.leetcode.problem.entity.Difficulty;
import com.rohit.leetcode.submission.entity.Submission.SubmissionStatus;
import com.rohit.leetcode.submission.repository.SubmissionRepository;
import com.rohit.leetcode.user.dto.UserStatsResponse;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;
import com.rohit.leetcode.user.service.UserStatsService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStatsServiceImpl
        implements UserStatsService {

    private final UserRepository userRepository;

    private final SubmissionRepository submissionRepository;


    // =========================================================
    // MY STATS
    // =========================================================

    @Override
    public UserStatsResponse getMyStats() {

        User user = getCurrentUser();

        return buildStats(user);
    }


    // =========================================================
    // USER STATS
    // =========================================================

    @Override
    public UserStatsResponse getUserStats(
            Long userId) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        return buildStats(user);
    }


    // =========================================================
    // BUILD USER STATISTICS
    // =========================================================

    private UserStatsResponse buildStats(
            User user) {

        Long userId = user.getId();


        // -----------------------------------------------------
        // TOTAL SUBMISSIONS
        // -----------------------------------------------------

        long totalSubmissions =
                submissionRepository.countByUserId(
                        userId
                );


        // -----------------------------------------------------
        // ACCEPTED
        // -----------------------------------------------------

        long accepted =
                submissionRepository
                        .countByUserIdAndStatus(
                                userId,
                                SubmissionStatus.ACCEPTED
                        );


        // -----------------------------------------------------
        // WRONG ANSWER
        // -----------------------------------------------------

        long wrongAnswer =
                submissionRepository
                        .countByUserIdAndStatus(
                                userId,
                                SubmissionStatus.WRONG_ANSWER
                        );


        // -----------------------------------------------------
        // COMPILATION ERROR
        // -----------------------------------------------------

        long compilationError =
                submissionRepository
                        .countByUserIdAndStatus(
                                userId,
                                SubmissionStatus.COMPILATION_ERROR
                        );


        // -----------------------------------------------------
        // RUNTIME ERROR
        // -----------------------------------------------------

        long runtimeError =
                submissionRepository
                        .countByUserIdAndStatus(
                                userId,
                                SubmissionStatus.RUNTIME_ERROR
                        );


        // -----------------------------------------------------
        // TOTAL UNIQUE PROBLEMS SOLVED
        // -----------------------------------------------------

        long totalSolved =
                submissionRepository
                        .countDistinctProblemsSolvedByUser(
                                userId,
                                SubmissionStatus.ACCEPTED
                        );


        // -----------------------------------------------------
        // EASY
        // -----------------------------------------------------

        long easySolved =
                submissionRepository
                        .countDistinctProblemsSolvedByUserAndDifficulty(
                                userId,
                                SubmissionStatus.ACCEPTED,
                                Difficulty.EASY
                        );


        // -----------------------------------------------------
        // MEDIUM
        // -----------------------------------------------------

        long mediumSolved =
                submissionRepository
                        .countDistinctProblemsSolvedByUserAndDifficulty(
                                userId,
                                SubmissionStatus.ACCEPTED,
                                Difficulty.MEDIUM
                        );


        // -----------------------------------------------------
        // HARD
        // -----------------------------------------------------

        long hardSolved =
                submissionRepository
                        .countDistinctProblemsSolvedByUserAndDifficulty(
                                userId,
                                SubmissionStatus.ACCEPTED,
                                Difficulty.HARD
                        );


        // -----------------------------------------------------
        // ACCEPTANCE RATE
        // -----------------------------------------------------

        double acceptanceRate = 0.0;

        if (totalSubmissions > 0) {

            acceptanceRate =
                    ((double) accepted /
                            totalSubmissions) * 100.0;
        }

        acceptanceRate =
                Math.round(
                        acceptanceRate * 100.0
                ) / 100.0;


        // -----------------------------------------------------
        // BUILD RESPONSE
        // -----------------------------------------------------

        return UserStatsResponse.builder()

                .userId(userId)

                .username(
                        user.getUsername()
                )

                .totalSubmissions(
                        totalSubmissions
                )

                .acceptedSubmissions(
                        accepted
                )

                .wrongAnswerSubmissions(
                        wrongAnswer
                )

                .compilationErrorSubmissions(
                        compilationError
                )

                .runtimeErrorSubmissions(
                        runtimeError
                )

                .totalSolved(
                        totalSolved
                )

                .easySolved(
                        easySolved
                )

                .mediumSolved(
                        mediumSolved
                )

                .hardSolved(
                        hardSolved
                )

                .acceptanceRate(
                        acceptanceRate
                )

                // Ranking will be implemented
                // in the Leaderboard module.
                .rank(0L)

                .build();
    }


    // =========================================================
    // GET CURRENT USER
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