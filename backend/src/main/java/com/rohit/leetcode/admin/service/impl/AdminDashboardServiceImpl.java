package com.rohit.leetcode.admin.service.impl;

import com.rohit.leetcode.admin.dto.AdminDashboardResponse;
import com.rohit.leetcode.admin.repository.AdminDashboardRepository;
import com.rohit.leetcode.admin.service.AdminDashboardService;

import com.rohit.leetcode.problem.entity.Difficulty;
import com.rohit.leetcode.problem.testcase.repository.TestCaseRepository;
import com.rohit.leetcode.submission.entity.Submission;
import com.rohit.leetcode.submission.repository.SubmissionRepository;
import com.rohit.leetcode.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl
        implements AdminDashboardService {

    private final UserRepository userRepository;

    private final SubmissionRepository submissionRepository;

    private final TestCaseRepository testCaseRepository;

    private final AdminDashboardRepository dashboardRepository;


    @Override
    public AdminDashboardResponse getDashboard() {

        // =====================================================
        // USERS
        // =====================================================

        long totalUsers =
                userRepository.count();

        long activeUsers =
                userRepository.countByActiveTrue();


        // =====================================================
        // PROBLEMS
        // =====================================================

        long totalProblems =
                dashboardRepositoryCountProblems();


        long easyProblems =
                dashboardRepository.countProblemsByDifficulty(
                        Difficulty.EASY
                );


        long mediumProblems =
                dashboardRepository.countProblemsByDifficulty(
                        Difficulty.MEDIUM
                );


        long hardProblems =
                dashboardRepository.countProblemsByDifficulty(
                        Difficulty.HARD
                );


        // =====================================================
        // SUBMISSIONS
        // =====================================================

        long totalSubmissions =
                submissionRepository.count();


        long acceptedSubmissions =
                submissionRepository
                        .countByUserIdAndStatus(
                                -1L,
                                Submission.SubmissionStatus.ACCEPTED
                        );

        /*
         * We cannot use countByUserIdAndStatus(-1)
         * for platform-wide count.
         *
         * Therefore use repository platform methods below.
         */

        acceptedSubmissions =
                submissionRepository
                        .countByStatus(
                                Submission.SubmissionStatus.ACCEPTED
                        );


        long wrongAnswers =
                submissionRepository
                        .countByStatus(
                                Submission.SubmissionStatus.WRONG_ANSWER
                        );


        long compilationErrors =
                submissionRepository
                        .countByStatus(
                                Submission.SubmissionStatus.COMPILATION_ERROR
                        );


        long runtimeErrors =
                submissionRepository
                        .countByStatus(
                                Submission.SubmissionStatus.RUNTIME_ERROR
                        );


        double acceptanceRate =
                calculateRate(
                        acceptedSubmissions,
                        totalSubmissions
                );


        // =====================================================
        // TEST CASES
        // =====================================================

        long totalTestCases =
                testCaseRepository.count();


        long hiddenTestCases =
                testCaseRepository
                        .countByProblemIdAndHiddenTrue(
                                -1L
                        );

        /*
         * Platform-wide count is needed.
         */

        hiddenTestCases =
                testCaseRepository
                        .countByHiddenTrue();


        long publicTestCases =
                testCaseRepository
                        .countByHiddenFalse();


        // =====================================================
        // TOP PROBLEMS
        // =====================================================

        List<AdminDashboardResponse.ProblemStatistic>
                mostAttempted =
                mapProblemStatistics(
                        dashboardRepository
                                .findMostAttemptedProblems(5)
                );


        List<AdminDashboardResponse.ProblemStatistic>
                mostSolved =
                mapProblemStatistics(
                        dashboardRepository
                                .findMostSolvedProblems(5)
                );


        // =====================================================
        // RECENT SUBMISSIONS
        // =====================================================

        List<Submission> recent =
                submissionRepository
                        .findAll(
                                PageRequest.of(
                                        0,
                                        5,
                                        Sort.by(
                                                Sort.Direction.DESC,
                                                "createdAt"
                                        )
                                )
                        )
                        .getContent();


        List<AdminDashboardResponse.RecentSubmission>
                recentSubmissions =
                recent.stream()
                        .map(this::mapRecentSubmission)
                        .toList();


        // =====================================================
        // RESPONSE
        // =====================================================

        return AdminDashboardResponse.builder()

                .totalUsers(totalUsers)

                .activeUsers(activeUsers)

                .totalProblems(totalProblems)

                .easyProblems(easyProblems)

                .mediumProblems(mediumProblems)

                .hardProblems(hardProblems)

                .totalSubmissions(totalSubmissions)

                .acceptedSubmissions(
                        acceptedSubmissions
                )

                .wrongAnswerSubmissions(
                        wrongAnswers
                )

                .compilationErrorSubmissions(
                        compilationErrors
                )

                .runtimeErrorSubmissions(
                        runtimeErrors
                )

                .acceptanceRate(
                        acceptanceRate
                )

                .totalTestCases(
                        totalTestCases
                )

                .publicTestCases(
                        publicTestCases
                )

                .hiddenTestCases(
                        hiddenTestCases
                )

                .mostAttemptedProblems(
                        mostAttempted
                )

                .mostSolvedProblems(
                        mostSolved
                )

                .recentSubmissions(
                        recentSubmissions
                )

                .build();
    }


    // =========================================================
    // PROBLEM COUNT
    // =========================================================

    private long dashboardRepositoryCountProblems() {

        return dashboardRepository
                .countProblemsByDifficulty(
                        Difficulty.EASY
                )
                +
                dashboardRepository
                        .countProblemsByDifficulty(
                                Difficulty.MEDIUM
                        )
                +
                dashboardRepository
                        .countProblemsByDifficulty(
                                Difficulty.HARD
                        );
    }


    // =========================================================
    // PROBLEM STATISTICS
    // =========================================================

    private List<AdminDashboardResponse.ProblemStatistic>
    mapProblemStatistics(
            List<Object[]> rows) {

        return rows.stream()
                .map(row -> {

                    Long problemId =
                            (Long) row[0];

                    String title =
                            (String) row[1];

                    Difficulty difficulty =
                            (Difficulty) row[2];

                    long submissions =
                            ((Number) row[3])
                                    .longValue();

                    long accepted =
                            dashboardRepository
                                    .countAcceptedForProblem(
                                            problemId
                                    );

                    double rate =
                            calculateRate(
                                    accepted,
                                    submissions
                            );

                    return AdminDashboardResponse
                            .ProblemStatistic
                            .builder()

                            .problemId(
                                    problemId
                            )

                            .title(
                                    title
                            )

                            .difficulty(
                                    difficulty.name()
                            )

                            .submissions(
                                    submissions
                            )

                            .accepted(
                                    accepted
                            )

                            .acceptanceRate(
                                    rate
                            )

                            .build();
                })
                .toList();
    }


    // =========================================================
    // RECENT SUBMISSION
    // =========================================================

    private AdminDashboardResponse.RecentSubmission
    mapRecentSubmission(
            Submission submission) {

        return AdminDashboardResponse
                .RecentSubmission
                .builder()

                .submissionId(
                        submission.getId()
                )

                .problemId(
                        submission.getProblem().getId()
                )

                .problemTitle(
                        submission
                                .getProblem()
                                .getTitle()
                )

                .username(
                        submission
                                .getUser()
                                .getUsername()
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
                        submission.getExecutionTime()
                )

                .createdAt(
                        submission
                                .getCreatedAt()
                                .toString()
                )

                .build();
    }


    // =========================================================
    // RATE
    // =========================================================

    private double calculateRate(
            long accepted,
            long total) {

        if (total == 0) {
            return 0.0;
        }

        return Math.round(
                ((double) accepted / total) * 10000
        ) / 100.0;
    }
}