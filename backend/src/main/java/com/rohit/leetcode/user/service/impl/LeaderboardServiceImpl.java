package com.rohit.leetcode.user.service.impl;

import com.rohit.leetcode.problem.entity.Difficulty;
import com.rohit.leetcode.submission.entity.Submission.SubmissionStatus;
import com.rohit.leetcode.submission.repository.SubmissionRepository;
import com.rohit.leetcode.user.dto.LeaderboardResponse;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;
import com.rohit.leetcode.user.service.LeaderboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaderboardServiceImpl
        implements LeaderboardService {

    private final UserRepository userRepository;

    private final SubmissionRepository submissionRepository;


    // =========================================================
    // GLOBAL LEADERBOARD
    // =========================================================

    @Override
    public List<LeaderboardResponse> getGlobalLeaderboard() {

        List<User> users =
                userRepository.findAll();

        List<LeaderboardResponse> leaderboard =
                new ArrayList<>();

        for (User user : users) {

            leaderboard.add(
                    buildLeaderboardEntry(user)
            );
        }

        // Highest score first
        leaderboard.sort(
                Comparator
                        .comparingLong(
                                LeaderboardResponse::getScore
                        )
                        .reversed()

                        // Tie breaker
                        .thenComparingLong(
                                LeaderboardResponse::getTotalSolved
                        )
                        .reversed()
        );

        assignRanks(leaderboard);

        return leaderboard;
    }


    // =========================================================
    // TOP USERS
    // =========================================================

    @Override
    public List<LeaderboardResponse> getTopUsers(
            int limit) {

        if (limit <= 0) {
            limit = 10;
        }

        List<LeaderboardResponse> leaderboard =
                getGlobalLeaderboard();

        if (leaderboard.size() <= limit) {
            return leaderboard;
        }

        return leaderboard.subList(
                0,
                limit
        );
    }


    // =========================================================
    // CURRENT USER RANK
    // =========================================================

    @Override
    public LeaderboardResponse getMyRank() {

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

        User currentUser =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Current user not found"
                                )
                        );

        List<LeaderboardResponse> leaderboard =
                getGlobalLeaderboard();

        return leaderboard.stream()
                .filter(entry ->
                        entry.getUserId()
                                .equals(currentUser.getId())
                )
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found in leaderboard"
                        )
                );
    }


    // =========================================================
    // BUILD LEADERBOARD ENTRY
    // =========================================================

    private LeaderboardResponse buildLeaderboardEntry(
            User user) {

        Long userId = user.getId();


        // -----------------------------------------------------
        // SUBMISSIONS
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
        // SOLVED
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

        long easy =
                submissionRepository
                        .countDistinctProblemsSolvedByUserAndDifficulty(
                                userId,
                                SubmissionStatus.ACCEPTED,
                                Difficulty.EASY
                        );


        // -----------------------------------------------------
        // MEDIUM
        // -----------------------------------------------------

        long medium =
                submissionRepository
                        .countDistinctProblemsSolvedByUserAndDifficulty(
                                userId,
                                SubmissionStatus.ACCEPTED,
                                Difficulty.MEDIUM
                        );


        // -----------------------------------------------------
        // HARD
        // -----------------------------------------------------

        long hard =
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
        // SCORE
        // -----------------------------------------------------

        long score =
                calculateScore(
                        easy,
                        medium,
                        hard
                );


        return LeaderboardResponse.builder()

                .userId(userId)

                .username(
                        user.getUsername()
                )

                .totalSolved(
                        totalSolved
                )

                .easySolved(
                        easy
                )

                .mediumSolved(
                        medium
                )

                .hardSolved(
                        hard
                )

                .totalSubmissions(
                        totalSubmissions
                )

                .acceptedSubmissions(
                        accepted
                )

                .acceptanceRate(
                        acceptanceRate
                )

                .score(
                        score
                )

                .build();
    }


    // =========================================================
    // SCORE SYSTEM
    // =========================================================

    private long calculateScore(
            long easy,
            long medium,
            long hard) {

        /*
         * Easy   = 10 points
         * Medium = 30 points
         * Hard   = 60 points
         */

        return
                (easy * 10)
                        +
                        (medium * 30)
                        +
                        (hard * 60);
    }


    // =========================================================
    // ASSIGN RANKS
    // =========================================================

    private void assignRanks(
            List<LeaderboardResponse> leaderboard) {

        long rank = 1;

        for (LeaderboardResponse entry :
                leaderboard) {

            entry.setRank(rank++);

        }
    }
}