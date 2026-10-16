package com.rohit.leetcode.progress.service.impl;

import com.rohit.leetcode.problem.entity.Difficulty;
import com.rohit.leetcode.progress.dto.DifficultyProgress;
import com.rohit.leetcode.progress.dto.TopicProgress;
import com.rohit.leetcode.progress.dto.UserProgressResponse;
import com.rohit.leetcode.progress.repository.ProgressRepository;
import com.rohit.leetcode.progress.service.ProgressService;
import com.rohit.leetcode.submission.entity.Submission;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressServiceImpl implements ProgressService {

    private final ProgressRepository progressRepository;

    private final UserRepository userRepository;


    // =========================================================
    // MAIN PROGRESS
    // =========================================================

    @Override
    public UserProgressResponse getMyProgress() {

        User user = getCurrentUser();

        Long userId = user.getId();


        // =====================================================
        // PROBLEMS
        // =====================================================

        long totalProblems =
                progressRepository.countProblems();

        long attemptedProblems =
                progressRepository.countAttemptedProblems(
                        userId
                );

        long solvedProblems =
                progressRepository.countSolvedProblems(
                        userId
                );

        long remainingProblems =
                Math.max(
                        0,
                        totalProblems - solvedProblems
                );


        // =====================================================
        // SUBMISSIONS
        // =====================================================

        long totalSubmissions =
                progressRepository.countSubmissions(
                        userId
                );

        long acceptedSubmissions =
                progressRepository.countSubmissionsByStatus(
                        userId,
                        Submission.SubmissionStatus.ACCEPTED
                );

        long wrongAnswerSubmissions =
                progressRepository.countSubmissionsByStatus(
                        userId,
                        Submission.SubmissionStatus.WRONG_ANSWER
                );

        long compilationErrors =
                progressRepository.countSubmissionsByStatus(
                        userId,
                        Submission.SubmissionStatus.COMPILATION_ERROR
                );

        long runtimeErrors =
                progressRepository.countSubmissionsByStatus(
                        userId,
                        Submission.SubmissionStatus.RUNTIME_ERROR
                );


        double acceptanceRate = 0;

        if (totalSubmissions > 0) {

            acceptanceRate =
                    Math.round(
                            (
                                    (double) acceptedSubmissions
                                            / totalSubmissions
                            ) * 10000
                    ) / 100.0;
        }


        // =====================================================
        // DIFFICULTY
        // =====================================================

        DifficultyProgress easy =
                buildDifficultyProgress(
                        userId,
                        Difficulty.EASY
                );

        DifficultyProgress medium =
                buildDifficultyProgress(
                        userId,
                        Difficulty.MEDIUM
                );

        DifficultyProgress hard =
                buildDifficultyProgress(
                        userId,
                        Difficulty.HARD
                );


        // =====================================================
        // TOPICS
        // =====================================================

        List<TopicProgress> topics =
                buildTopicProgress(userId);


        // =====================================================
        // RESPONSE
        // =====================================================

        return UserProgressResponse.builder()

                .totalProblems(totalProblems)

                .attemptedProblems(attemptedProblems)

                .solvedProblems(solvedProblems)

                .remainingProblems(remainingProblems)

                .totalSubmissions(totalSubmissions)

                .acceptedSubmissions(acceptedSubmissions)

                .wrongAnswerSubmissions(
                        wrongAnswerSubmissions
                )

                .compilationErrorSubmissions(
                        compilationErrors
                )

                .runtimeErrorSubmissions(
                        runtimeErrors
                )

                .acceptanceRate(acceptanceRate)

                .easy(easy)

                .medium(medium)

                .hard(hard)

                .topicProgress(topics)

                .build();
    }


    // =========================================================
    // DIFFICULTY PROGRESS
    // =========================================================

    private DifficultyProgress buildDifficultyProgress(
            Long userId,
            Difficulty difficulty
    ) {

        long total =
                progressRepository
                        .countProblemsByDifficulty(
                                difficulty
                        );

        long solved =
                progressRepository
                        .countSolvedProblemsByDifficulty(
                                userId,
                                difficulty
                        );

        long attempted =
                progressRepository
                        .countAttemptedProblemsByDifficulty(
                                userId,
                                difficulty
                        );

        long percentage =
                calculatePercentage(
                        solved,
                        total
                );

        return DifficultyProgress.builder()

                .difficulty(
                        difficulty.name()
                )

                .totalProblems(total)

                .solvedProblems(solved)

                .attemptedProblems(attempted)

                .solvedPercentage(percentage)

                .build();
    }


    // =========================================================
    // TOPIC PROGRESS
    // =========================================================

    private List<TopicProgress> buildTopicProgress(
            Long userId
    ) {

        List<Object[]> rows =
                progressRepository
                        .findTopicStatistics(userId);

        List<TopicProgress> result =
                new ArrayList<>();

        for (Object[] row : rows) {

            String topic =
                    row[0] != null
                            ? row[0].toString()
                            : "Unknown";

            long total =
                    ((Number) row[1]).longValue();

            long solved =
                    progressRepository
                            .countSolvedProblemsByTopic(
                                    userId,
                                    topic
                            );

            long attempted =
                    progressRepository
                            .countAttemptedProblemsByTopic(
                                    userId,
                                    topic
                            );

            long percentage =
                    calculatePercentage(
                            solved,
                            total
                    );

            result.add(
                    TopicProgress.builder()

                            .topic(topic)

                            .totalProblems(total)

                            .solvedProblems(solved)

                            .attemptedProblems(
                                    attempted
                            )

                            .solvedPercentage(
                                    percentage
                            )

                            .build()
            );
        }

        return result;
    }


    // =========================================================
    // PERCENTAGE
    // =========================================================

    private long calculatePercentage(
            long value,
            long total
    ) {

        if (total == 0) {
            return 0;
        }

        return Math.round(
                ((double) value / total) * 100
        );
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
                !authentication.isAuthenticated()) {

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
                                "Authenticated user not found"
                        )
                );
    }
}