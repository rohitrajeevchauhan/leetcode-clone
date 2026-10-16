package com.rohit.leetcode.submission.service.impl;

import com.rohit.leetcode.platform.gamification.service.GamificationService;
import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.problem.repository.ProblemRepository;
import com.rohit.leetcode.submission.dto.SubmissionRequest;
import com.rohit.leetcode.submission.dto.SubmissionResponse;
import com.rohit.leetcode.submission.dto.TestCaseResultResponse;
import com.rohit.leetcode.submission.entity.Submission;
import com.rohit.leetcode.submission.entity.Submission.SubmissionStatus;
import com.rohit.leetcode.submission.judge.JudgeResult;
import com.rohit.leetcode.submission.judge.MultiLanguageJudgeService;
import com.rohit.leetcode.submission.judge.TestCaseResult;
import com.rohit.leetcode.submission.repository.SubmissionRepository;
import com.rohit.leetcode.submission.service.SubmissionService;
import com.rohit.leetcode.problem.testcase.entity.TestCase;
import com.rohit.leetcode.problem.testcase.repository.TestCaseRepository;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rohit.leetcode.submission.dto.RunResponse;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final MultiLanguageJudgeService judgeService;
    private final GamificationService gamificationService;

    @Override
    public SubmissionResponse submit(SubmissionRequest request) {

        // 1. Validate request
        if (request == null) {
            throw new IllegalArgumentException(
                    "Submission request cannot be null"
            );
        }

        if (request.getProblemId() == null) {
            throw new IllegalArgumentException(
                    "Problem ID is required"
            );
        }

        if (request.getSourceCode() == null
                || request.getSourceCode().isBlank()) {
            throw new IllegalArgumentException(
                    "Source code cannot be empty"
            );
        }

        if (request.getLanguage() == null
                || request.getLanguage().isBlank()) {
            throw new IllegalArgumentException(
                    "Programming language is required"
            );
        }

        // 2. Get authenticated user
        User user = getCurrentUser();

        // 3. Find problem
        Problem problem = problemRepository
                .findById(request.getProblemId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Problem not found with ID: "
                                        + request.getProblemId()
                        )
                );

        // 4. Validate programming language
        Submission.ProgrammingLanguage language;

        try {
            language = Submission.ProgrammingLanguage.valueOf(
                    request.getLanguage().trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Unsupported programming language: "
                            + request.getLanguage()
            );
        }

        // 5. Create submission
        Submission submission = Submission.builder()
                .code(request.getSourceCode())
                .language(language)
                .status(SubmissionStatus.PENDING)
                .user(user)
                .problem(problem)
                .build();

        submission = submissionRepository.save(submission);

        // 6. Load test cases
        List<TestCase> testCases =
                testCaseRepository.findByProblemIdOrderByIdAsc(
                        problem.getId()
                );

        // 7. Handle missing test cases
        if (testCases.isEmpty()) {
            submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
            submission.setErrorMessage(
                    "No test cases configured for this problem"
            );
            submission.setOutput("");

            submissionRepository.save(submission);

            return mapToResponse(submission, List.of());
        }

        // 8. Mark submission as running
        submission.setStatus(SubmissionStatus.RUNNING);
        submissionRepository.save(submission);

        // 9. Execute code against test cases
        JudgeResult result = judgeService.judge(
                request.getSourceCode(),
                language,
                testCases
        );

        // 10. Store judging results
        submission.setStatus(result.status());
        submission.setOutput(result.output());
        submission.setErrorMessage(result.errorMessage());
        submission.setExecutionTime(result.executionTime());
        submission.setMemoryUsed(result.memoryUsed());

        submission = submissionRepository.save(submission);

        // 11. Award gamification XP after judging
        // The gamification service ensures each problem rewards XP
        // only once per user.
        if (submission.getStatus() == SubmissionStatus.ACCEPTED) {
            gamificationService.awardAcceptedSubmission(
                    user,
                    problem
            );
        }

        // 12. Return submission response
        return mapToResponse(
                submission,
                result.testCases()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionById(Long id) {

        User user = getCurrentUser();

        Submission submission = submissionRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Submission not found with ID: " + id
                        )
                );

        // Users can only view their own submissions.
        if (!submission.getUser().getId().equals(user.getId())) {
            throw new SecurityException(
                    "You are not authorized to view this submission"
            );
        }

        return mapToResponse(submission, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubmissionResponse> getMySubmissions(
            Pageable pageable
    ) {
        User user = getCurrentUser();

        return submissionRepository
                .findByUserId(user.getId(), pageable)
                .map(submission ->
                        mapToResponse(submission, List.of())
                );
    }


    @Override
    public RunResponse runCode(SubmissionRequest request) {

        if (request == null
                || request.getProblemId() == null
                || request.getSourceCode() == null
                || request.getSourceCode().isBlank()
                || request.getLanguage() == null
                || request.getLanguage().isBlank()) {

            throw new IllegalArgumentException(
                    "Problem ID, source code, and language are required."
            );
        }

        User user = getCurrentUser();

        Problem problem = problemRepository
                .findById(request.getProblemId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Problem not found.")
                );

        Submission.ProgrammingLanguage language;

        try {
            language = Submission.ProgrammingLanguage.valueOf(
                    request.getLanguage().trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Unsupported programming language: "
                            + request.getLanguage()
            );
        }

        List<TestCase> publicTestCases =
                testCaseRepository
                        .findByProblemIdAndHiddenFalseOrderByIdAsc(
                                problem.getId()
                        );

        if (publicTestCases.isEmpty()) {
            throw new IllegalArgumentException(
                    "No public test cases are configured for this problem."
            );
        }

        JudgeResult result = judgeService.judge(
                request.getSourceCode(),
                language,
                publicTestCases
        );

        return RunResponse.builder()
                .status(result.status())
                .output(result.output())
                .errorMessage(result.errorMessage())
                .executionTime(result.executionTime())
                .testCases(result.testCases())
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public Page<SubmissionResponse> getMyProblemSubmissions(
            Long problemId,
            Pageable pageable
    ) {
        User user = getCurrentUser();

        return submissionRepository
                .findByUserIdAndProblemId(
                        user.getId(),
                        problemId,
                        pageable
                )
                .map(submission ->
                        mapToResponse(submission, List.of())
                );
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal() == null
                || "anonymousUser".equals(
                authentication.getPrincipal()
        )) {
            throw new SecurityException(
                    "You must be logged in to submit or view submissions"
            );
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new SecurityException(
                                "Authenticated user was not found"
                        )
                );
    }

    private SubmissionResponse mapToResponse(
            Submission submission,
            List<TestCaseResult> testCaseResults
    ) {

        List<TestCaseResultResponse> testCaseResponses =
                testCaseResults == null
                        ? List.of()
                        : testCaseResults.stream()
                        .map(this::mapTestCaseResult)
                        .toList();

        long passedTestCases = testCaseResponses.stream()
                .filter(TestCaseResultResponse::isPassed)
                .count();

        return SubmissionResponse.builder()
                .id(submission.getId())
                .problemId(submission.getProblem().getId())
                .problemTitle(submission.getProblem().getTitle())
                .language(submission.getLanguage().name())
                .status(submission.getStatus().name())
                .code(submission.getCode())
                .output(submission.getOutput())
                .errorMessage(submission.getErrorMessage())
                .executionTime(submission.getExecutionTime())
                .memoryUsed(submission.getMemoryUsed())
                .createdAt(formatDate(submission.getCreatedAt()))
                .updatedAt(formatDate(submission.getUpdatedAt()))
                .totalTestCases(testCaseResponses.size())
                .passedTestCases((int) passedTestCases)
                .testCases(testCaseResponses)
                .build();
    }

    private TestCaseResultResponse mapTestCaseResult(
            TestCaseResult result
    ) {
        return TestCaseResultResponse.builder()
                .testCaseId(result.testCaseId())
                .passed(result.passed())
                .input(result.input())
                .expectedOutput(result.expectedOutput())
                .actualOutput(result.actualOutput())
                .error(result.error())
                .executionTime(result.executionTime())
                .build();
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toString();
    }
}