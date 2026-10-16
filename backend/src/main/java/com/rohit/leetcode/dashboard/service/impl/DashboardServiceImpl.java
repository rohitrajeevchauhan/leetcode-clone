package com.rohit.leetcode.dashboard.service.impl;

import com.rohit.leetcode.dashboard.dto.DailyActivityResponse;
import com.rohit.leetcode.dashboard.dto.DashboardResponse;
import com.rohit.leetcode.dashboard.dto.RecentFavoriteResponse;
import com.rohit.leetcode.dashboard.dto.RecentSubmissionResponse;
import com.rohit.leetcode.dashboard.repository.DashboardRepository;
import com.rohit.leetcode.dashboard.service.DashboardService;

import com.rohit.leetcode.favorite.entity.Favorite;

import com.rohit.leetcode.problem.repository.ProblemRepository;

import com.rohit.leetcode.submission.entity.Submission;
import com.rohit.leetcode.submission.repository.SubmissionRepository;

import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl
        implements DashboardService {

    private final UserRepository userRepository;

    private final ProblemRepository problemRepository;

    private final SubmissionRepository submissionRepository;

    private final DashboardRepository dashboardRepository;


    // =========================================================
    // MAIN DASHBOARD
    // =========================================================

    @Override
    public DashboardResponse getMyDashboard() {

        User user = getCurrentUser();

        Long userId = user.getId();


        // =====================================================
        // PROBLEM STATISTICS
        // =====================================================

        long totalProblems =
                problemRepository.count();

        long attemptedProblems =
                countAttemptedProblems(userId);

        long solvedProblems =
                countSolvedProblems(userId);

        long remainingProblems =
                Math.max(
                        0,
                        totalProblems - solvedProblems
                );


        // =====================================================
        // SUBMISSION STATISTICS
        // =====================================================

        long totalSubmissions =
                submissionRepository.countByUserId(
                        userId
                );

        long acceptedSubmissions =
                submissionRepository.countByUserIdAndStatus(
                        userId,
                        Submission.SubmissionStatus.ACCEPTED
                );

        long wrongAnswerSubmissions =
                submissionRepository.countByUserIdAndStatus(
                        userId,
                        Submission.SubmissionStatus.WRONG_ANSWER
                );

        long compilationErrorSubmissions =
                submissionRepository.countByUserIdAndStatus(
                        userId,
                        Submission.SubmissionStatus.COMPILATION_ERROR
                );

        long runtimeErrorSubmissions =
                submissionRepository.countByUserIdAndStatus(
                        userId,
                        Submission.SubmissionStatus.RUNTIME_ERROR
                );


        double acceptanceRate =
                calculateAcceptanceRate(
                        acceptedSubmissions,
                        totalSubmissions
                );


        // =====================================================
        // ACTIVITY
        // =====================================================

        List<Submission> allSubmissions =
                dashboardRepository.findUserSubmissions(
                        userId
                );

        Map<LocalDate, DailyActivityData> activity =
                buildActivityMap(
                        allSubmissions
                );


        // =====================================================
        // STREAK
        // =====================================================

        int currentStreak =
                calculateCurrentStreak(
                        activity
                );

        int longestStreak =
                calculateLongestStreak(
                        activity
                );

        long totalActiveDays =
                activity.size();

        LocalDate today =
                LocalDate.now();

        boolean activeToday =
                activity.containsKey(today);


        // =====================================================
        // RECENT SUBMISSIONS
        // =====================================================

        List<RecentSubmissionResponse>
                recentSubmissions =
                dashboardRepository
                        .findRecentSubmissions(
                                userId,
                                10
                        )
                        .stream()
                        .map(this::mapSubmission)
                        .toList();


        // =====================================================
        // RECENT FAVORITES
        // =====================================================

        List<RecentFavoriteResponse>
                recentFavorites =
                dashboardRepository
                        .findRecentFavorites(
                                userId,
                                10
                        )
                        .stream()
                        .map(this::mapFavorite)
                        .toList();


        // =====================================================
        // DAILY ACTIVITY
        // =====================================================

        List<DailyActivityResponse>
                dailyActivity =
                buildDailyActivity(
                        activity
                );


        // =====================================================
        // RESPONSE
        // =====================================================

        return DashboardResponse.builder()

                .userId(user.getId())

                .username(user.getUsername())

                .email(user.getEmail())

                .role(user.getRole().name())

                .totalProblems(totalProblems)

                .attemptedProblems(attemptedProblems)

                .solvedProblems(solvedProblems)

                .remainingProblems(remainingProblems)

                .totalSubmissions(totalSubmissions)

                .acceptedSubmissions(
                        acceptedSubmissions
                )

                .wrongAnswerSubmissions(
                        wrongAnswerSubmissions
                )

                .compilationErrorSubmissions(
                        compilationErrorSubmissions
                )

                .runtimeErrorSubmissions(
                        runtimeErrorSubmissions
                )

                .acceptanceRate(acceptanceRate)

                .currentStreak(currentStreak)

                .longestStreak(longestStreak)

                .totalActiveDays(totalActiveDays)

                .activeToday(activeToday)

                .recentSubmissions(
                        recentSubmissions
                )

                .recentFavorites(
                        recentFavorites
                )

                .dailyActivity(
                        dailyActivity
                )

                .build();
    }


    // =========================================================
    // ATTEMPTED PROBLEMS
    // =========================================================

    private long countAttemptedProblems(
            Long userId
    ) {

        return submissionRepository
                .findByUserIdOrderByCreatedAtDesc(
                        userId
                )
                .stream()
                .map(submission ->
                        submission
                                .getProblem()
                                .getId()
                )
                .distinct()
                .count();
    }


    // =========================================================
    // SOLVED PROBLEMS
    // =========================================================

    private long countSolvedProblems(
            Long userId
    ) {

        return submissionRepository
                .findByUserIdOrderByCreatedAtDesc(
                        userId
                )
                .stream()
                .filter(submission ->
                        submission.getStatus()
                                == Submission.SubmissionStatus
                                .ACCEPTED
                )
                .map(submission ->
                        submission
                                .getProblem()
                                .getId()
                )
                .distinct()
                .count();
    }


    // =========================================================
    // ACCEPTANCE RATE
    // =========================================================

    private double calculateAcceptanceRate(
            long accepted,
            long total
    ) {

        if (total == 0) {
            return 0;
        }

        return Math.round(
                (
                        (double) accepted / total
                ) * 10000
        ) / 100.0;
    }


    // =========================================================
    // BUILD ACTIVITY MAP
    // =========================================================

    private Map<LocalDate, DailyActivityData>
    buildActivityMap(
            List<Submission> submissions
    ) {

        Map<LocalDate, DailyActivityData>
                activity =
                new LinkedHashMap<>();

        for (Submission submission :
                submissions) {

            if (submission.getCreatedAt() == null) {
                continue;
            }

            LocalDate date =
                    submission
                            .getCreatedAt()
                            .toLocalDate();

            DailyActivityData data =
                    activity.computeIfAbsent(
                            date,
                            key ->
                                    new DailyActivityData()
                    );

            data.submissionCount++;

            if (submission.getStatus()
                    == Submission.SubmissionStatus.ACCEPTED) {

                data.acceptedCount++;
            }
        }

        return activity;
    }


    // =========================================================
    // CURRENT STREAK
    // =========================================================

    private int calculateCurrentStreak(
            Map<LocalDate, DailyActivityData> activity
    ) {

        if (activity.isEmpty()) {
            return 0;
        }

        LocalDate today =
                LocalDate.now();

        LocalDate currentDate = today;

        /*
         * If user has not submitted today,
         * start counting from yesterday.
         */
        if (!activity.containsKey(currentDate)) {

            currentDate =
                    currentDate.minusDays(1);

            if (!activity.containsKey(currentDate)) {
                return 0;
            }
        }

        int streak = 0;

        while (activity.containsKey(currentDate)) {

            streak++;

            currentDate =
                    currentDate.minusDays(1);
        }

        return streak;
    }


    // =========================================================
    // LONGEST STREAK
    // =========================================================

    private int calculateLongestStreak(
            Map<LocalDate, DailyActivityData> activity
    ) {

        if (activity.isEmpty()) {
            return 0;
        }

        List<LocalDate> dates =
                new ArrayList<>(
                        activity.keySet()
                );

        Collections.sort(dates);

        int longest = 1;

        int current = 1;

        for (int i = 1;
             i < dates.size();
             i++) {

            LocalDate previous =
                    dates.get(i - 1);

            LocalDate currentDate =
                    dates.get(i);

            if (previous
                    .plusDays(1)
                    .equals(currentDate)) {

                current++;

                longest =
                        Math.max(
                                longest,
                                current
                        );

            } else {

                current = 1;
            }
        }

        return longest;
    }


    // =========================================================
    // DAILY ACTIVITY
    // =========================================================

    private List<DailyActivityResponse>
    buildDailyActivity(
            Map<LocalDate, DailyActivityData>
                    activity
    ) {

        return activity
                .entrySet()
                .stream()
                .map(entry ->
                        DailyActivityResponse
                                .builder()
                                .date(entry.getKey())
                                .submissionCount(
                                        entry.getValue()
                                                .submissionCount
                                )
                                .acceptedCount(
                                        entry.getValue()
                                                .acceptedCount
                                )
                                .build()
                )
                .toList();
    }


    // =========================================================
    // SUBMISSION → DTO
    // =========================================================

    private RecentSubmissionResponse
    mapSubmission(
            Submission submission
    ) {

        return RecentSubmissionResponse
                .builder()

                .submissionId(
                        submission.getId()
                )

                .problemId(
                        submission
                                .getProblem()
                                .getId()
                )

                .problemTitle(
                        submission
                                .getProblem()
                                .getTitle()
                )

                .language(
                        submission
                                .getLanguage()
                                .name()
                )

                .status(
                        submission
                                .getStatus()
                                .name()
                )

                .executionTime(
                        submission
                                .getExecutionTime()
                )

                .createdAt(
                        submission
                                .getCreatedAt()
                )

                .build();
    }


    // =========================================================
    // FAVORITE → DTO
    // =========================================================

    private RecentFavoriteResponse
    mapFavorite(
            Favorite favorite
    ) {

        return RecentFavoriteResponse
                .builder()

                .problemId(
                        favorite
                                .getProblem()
                                .getId()
                )

                .problemTitle(
                        favorite
                                .getProblem()
                                .getTitle()
                )

                .difficulty(
                        favorite
                                .getProblem()
                                .getDifficulty() != null
                                ? favorite
                                .getProblem()
                                .getDifficulty()
                                .name()
                                : null
                )

                .topic(
                        favorite
                                .getProblem()
                                .getTopic()
                )

                .createdAt(
                        favorite
                                .getCreatedAt()
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


    // =========================================================
    // INTERNAL ACTIVITY DATA
    // =========================================================

    private static class DailyActivityData {

        private long submissionCount;

        private long acceptedCount;
    }
}