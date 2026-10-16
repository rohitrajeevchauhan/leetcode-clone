package com.rohit.leetcode.contest.service.impl;

import com.rohit.leetcode.contest.dto.*;
import com.rohit.leetcode.contest.entity.*;
import com.rohit.leetcode.contest.repository.*;
import com.rohit.leetcode.contest.service.ContestSubmissionService;
import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.problem.repository.ProblemRepository;
import com.rohit.leetcode.problem.testcase.entity.TestCase;
import com.rohit.leetcode.problem.testcase.repository.TestCaseRepository;
import com.rohit.leetcode.submission.judge.*;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ContestSubmissionServiceImpl
        implements ContestSubmissionService {

    private final ContestRepository contestRepository;
    private final ContestProblemRepository contestProblemRepository;
    private final ContestRegistrationRepository registrationRepository;
    private final ContestSubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final UserRepository userRepository;

    private final MultiLanguageJudgeService judgeService;

    @Override
    public ContestSubmissionResponse submit(
            ContestSubmissionRequest request) {

        Contest contest = contestRepository.findById(request.getContestId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Contest not found"));

        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Problem not found"));

        User user = getCurrentUser();

        contest.updateStatus();

        if (contest.getStatus() != ContestStatus.RUNNING) {
            throw new IllegalStateException(
                    "Contest is not currently running"
            );
        }

        boolean registered =
                registrationRepository.existsByContestIdAndUserId(
                        contest.getId(),
                        user.getId()
                );

        if (!registered) {
            throw new IllegalStateException(
                    "You must register for the contest first"
            );
        }

        ContestProblem contestProblem =
                contestProblemRepository
                        .findByContestIdAndProblemId(
                                contest.getId(),
                                problem.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Problem is not part of this contest"
                                ));

        ContestSubmission.ProgrammingLanguage language =
                parseLanguage(request.getLanguage());

        int attemptNumber =
                (int) submissionRepository
                        .countByContestIdAndUserIdAndProblemId(
                                contest.getId(),
                                user.getId(),
                                problem.getId()
                        ) + 1;

        ContestSubmission submission =
                ContestSubmission.builder()
                        .contest(contest)
                        .user(user)
                        .problem(problem)
                        .language(language)
                        .code(request.getSourceCode())
                        .attemptNumber(attemptNumber)
                        .status(
                                ContestSubmission.SubmissionStatus.RUNNING
                        )
                        .build();

        submission = submissionRepository.save(submission);

        List<TestCase> testCases =
                testCaseRepository
                        .findByProblemIdOrderByIdAsc(problem.getId());

        if (testCases.isEmpty()) {

            submission.setStatus(
                    ContestSubmission.SubmissionStatus.RUNTIME_ERROR
            );

            submission.setErrorMessage(
                    "No test cases configured for this problem"
            );

            return mapResponse(
                    submissionRepository.save(submission)
            );
        }

        JudgeResult result =
                judgeService.judge(
                        request.getSourceCode(),
                        convertLanguage(language),
                        testCases
                );

        submission.setStatus(
                convertStatus(result.status())
        );

        submission.setOutput(result.output());
        submission.setErrorMessage(result.errorMessage());
        submission.setExecutionTime(result.executionTime());
        submission.setMemoryUsed(result.memoryUsed());

        if (result.status() ==
                com.rohit.leetcode.submission.entity.Submission
                        .SubmissionStatus.ACCEPTED) {

            submission.setScore(
                    contestProblem.getPoints()
            );

            submission.setPenalty(
                    calculatePenalty(
                            contest,
                            submission.getCreatedAt()
                    )
            );
        } else {
            submission.setScore(0);
            submission.setPenalty(0);
        }

        return mapResponse(
                submissionRepository.save(submission)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContestSubmissionResponse> getMySubmissions(
            Long contestId) {

        User user = getCurrentUser();

        return submissionRepository
                .findByContestIdAndUserIdOrderByCreatedAtAsc(
                        contestId,
                        user.getId()
                )
                .stream()
                .map(this::mapResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContestLeaderboardResponse> getLeaderboard(
            Long contestId) {

        contestRepository.findById(contestId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Contest not found"
                        ));

        List<ContestSubmission> submissions =
                submissionRepository
                        .findByContestIdOrderByCreatedAtAsc(
                                contestId
                        );

        Map<Long, ContestLeaderboardData> leaderboard =
                new HashMap<>();

        for (ContestSubmission submission : submissions) {

            Long userId = submission.getUser().getId();

            ContestLeaderboardData data =
                    leaderboard.computeIfAbsent(
                            userId,
                            id -> new ContestLeaderboardData(
                                    userId,
                                    submission.getUser().getUsername()
                            )
                    );

            data.totalSubmissions++;

            if (submission.getStatus() ==
                    ContestSubmission.SubmissionStatus.ACCEPTED) {

                /*
                 * Count only the first accepted submission
                 * for a particular problem.
                 */
                boolean alreadySolved =
                        data.solvedProblems.contains(
                                submission.getProblem().getId()
                        );

                if (!alreadySolved) {

                    data.solvedProblems.add(
                            submission.getProblem().getId()
                    );

                    data.totalScore += submission.getScore();
                    data.totalPenalty +=
                            calculateProblemPenalty(
                                    submissions,
                                    submission
                            );
                }
            }
        }

        List<ContestLeaderboardResponse> result =
                leaderboard.values()
                        .stream()
                        .map(data ->
                                ContestLeaderboardResponse.builder()
                                        .userId(data.userId)
                                        .username(data.username)
                                        .totalScore(data.totalScore)
                                        .totalPenalty(data.totalPenalty)
                                        .solvedProblems(
                                                data.solvedProblems.size()
                                        )
                                        .totalSubmissions(
                                                data.totalSubmissions
                                        )
                                        .build()
                        )
                        .sorted(
                                Comparator
                                        .comparing(
                                                ContestLeaderboardResponse
                                                        ::getTotalScore
                                        )
                                        .reversed()
                                        .thenComparing(
                                                ContestLeaderboardResponse
                                                        ::getTotalPenalty
                                        )
                        )
                        .toList();

        for (int i = 0; i < result.size(); i++) {
            result.get(i).setRank(i + 1);
        }

        return result;
    }

    private int calculatePenalty(
            Contest contest,
            LocalDateTime submissionTime) {

        return (int) Duration.between(
                contest.getStartTime(),
                submissionTime
        ).toMinutes();
    }

    private int calculateProblemPenalty(
            List<ContestSubmission> submissions,
            ContestSubmission acceptedSubmission) {

        return calculatePenalty(
                acceptedSubmission.getContest(),
                acceptedSubmission.getCreatedAt()
        );
    }

    private ContestSubmission.ProgrammingLanguage parseLanguage(
            String language) {

        if (language == null) {
            throw new IllegalArgumentException(
                    "Language is required"
            );
        }

        return switch (language.trim().toUpperCase()) {
            case "JAVA" ->
                    ContestSubmission.ProgrammingLanguage.JAVA;

            case "CPP", "C++" ->
                    ContestSubmission.ProgrammingLanguage.CPP;

            case "PYTHON", "PY" ->
                    ContestSubmission.ProgrammingLanguage.PYTHON;

            case "JAVASCRIPT", "JS" ->
                    ContestSubmission.ProgrammingLanguage.JAVASCRIPT;

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported language: " + language
                    );
        };
    }

    private com.rohit.leetcode.submission.entity.Submission
            .ProgrammingLanguage convertLanguage(
            ContestSubmission.ProgrammingLanguage language) {

        return switch (language) {

            case JAVA ->
                    com.rohit.leetcode.submission.entity.Submission
                            .ProgrammingLanguage.JAVA;

            case CPP ->
                    com.rohit.leetcode.submission.entity.Submission
                            .ProgrammingLanguage.CPP;

            case PYTHON ->
                    com.rohit.leetcode.submission.entity.Submission
                            .ProgrammingLanguage.PYTHON;

            case JAVASCRIPT ->
                    com.rohit.leetcode.submission.entity.Submission
                            .ProgrammingLanguage.JAVASCRIPT;
        };
    }

    private ContestSubmission.SubmissionStatus convertStatus(
            com.rohit.leetcode.submission.entity.Submission.SubmissionStatus status) {

        return switch (status) {

            case PENDING ->
                    ContestSubmission.SubmissionStatus.PENDING;

            case RUNNING ->
                    ContestSubmission.SubmissionStatus.RUNNING;

            case ACCEPTED ->
                    ContestSubmission.SubmissionStatus.ACCEPTED;

            case WRONG_ANSWER ->
                    ContestSubmission.SubmissionStatus.WRONG_ANSWER;

            case COMPILATION_ERROR ->
                    ContestSubmission.SubmissionStatus.COMPILATION_ERROR;

            case RUNTIME_ERROR ->
                    ContestSubmission.SubmissionStatus.RUNTIME_ERROR;

            case TIME_LIMIT_EXCEEDED ->
                    ContestSubmission.SubmissionStatus.TIME_LIMIT_EXCEEDED;

            case MEMORY_LIMIT_EXCEEDED ->
                    ContestSubmission.SubmissionStatus.MEMORY_LIMIT_EXCEEDED;
        };
    }

    private ContestSubmissionResponse mapResponse(
            ContestSubmission submission) {

        return ContestSubmissionResponse.builder()
                .id(submission.getId())
                .contestId(submission.getContest().getId())
                .problemId(submission.getProblem().getId())
                .problemTitle(submission.getProblem().getTitle())
                .language(submission.getLanguage().name())
                .status(submission.getStatus().name())
                .output(submission.getOutput())
                .errorMessage(submission.getErrorMessage())
                .executionTime(submission.getExecutionTime())
                .memoryUsed(submission.getMemoryUsed())
                .score(submission.getScore())
                .penalty(submission.getPenalty())
                .attemptNumber(submission.getAttemptNumber())
                .createdAt(submission.getCreatedAt())
                .build();
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "User is not authenticated"
            );
        }

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User not found"
                        ));
    }

    private static class ContestLeaderboardData {

        Long userId;
        String username;

        int totalScore = 0;
        int totalPenalty = 0;
        int totalSubmissions = 0;

        Set<Long> solvedProblems =
                new HashSet<>();

        ContestLeaderboardData(
                Long userId,
                String username) {

            this.userId = userId;
            this.username = username;
        }
    }
}