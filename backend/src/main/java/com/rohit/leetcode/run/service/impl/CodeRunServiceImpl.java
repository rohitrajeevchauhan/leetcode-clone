package com.rohit.leetcode.run.service.impl;

import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.problem.repository.ProblemRepository;
import com.rohit.leetcode.problem.testcase.entity.TestCase;
import com.rohit.leetcode.problem.testcase.repository.TestCaseRepository;

import com.rohit.leetcode.run.dto.RunCodeRequest;
import com.rohit.leetcode.run.dto.RunCodeResponse;
import com.rohit.leetcode.run.dto.RunTestCaseResponse;
import com.rohit.leetcode.run.service.CodeRunService;

import com.rohit.leetcode.submission.entity.Submission;
import com.rohit.leetcode.submission.judge.JudgeResult;
import com.rohit.leetcode.submission.judge.MultiLanguageJudgeService;
import com.rohit.leetcode.submission.judge.TestCaseResult;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CodeRunServiceImpl implements CodeRunService {

    private final ProblemRepository problemRepository;

    private final TestCaseRepository testCaseRepository;

    private final MultiLanguageJudgeService judgeService;

    @Override
    public RunCodeResponse runCode(RunCodeRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Run code request cannot be null"
            );
        }

        // ==========================================
        // 1. FIND PROBLEM
        // ==========================================

        Problem problem = problemRepository
                .findById(request.getProblemId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Problem not found with id: "
                                        + request.getProblemId()
                        )
                );

        // ==========================================
        // 2. CONVERT LANGUAGE
        // ==========================================

        Submission.ProgrammingLanguage language =
                parseLanguage(request.getLanguage());

        // ==========================================
        // 3. GET PUBLIC TEST CASES
        // ==========================================

        List<TestCase> testCases =
                testCaseRepository
                        .findByProblemIdAndHiddenFalseOrderByIdAsc(
                                problem.getId()
                        );

        if (testCases.isEmpty()) {
            throw new IllegalStateException(
                    "No public test cases found for this problem"
            );
        }

        // ==========================================
        // 4. RUN CODE
        // ==========================================

        JudgeResult result =
                judgeService.judge(
                        request.getSourceCode(),
                        language,
                        testCases
                );

        // ==========================================
        // 5. MAP TEST CASE RESULTS
        // ==========================================

        List<RunTestCaseResponse> testCaseResponses =
                result.testCases()
                        .stream()
                        .map(this::mapTestCaseResult)
                        .toList();

        // ==========================================
        // 6. COUNT RESULTS
        // ==========================================

        int totalTestCases =
                testCaseResponses.size();

        int passedTestCases =
                (int) testCaseResponses
                        .stream()
                        .filter(RunTestCaseResponse::isPassed)
                        .count();

        // ==========================================
        // 7. RETURN RESPONSE
        // ==========================================

        return RunCodeResponse.builder()
                .problemId(problem.getId())
                .language(language.name())
                .status(result.status().name())
                .output(result.output())
                .errorMessage(result.errorMessage())
                .executionTime(result.executionTime())
                .memoryUsed(result.memoryUsed())
                .totalTestCases(totalTestCases)
                .passedTestCases(passedTestCases)
                .testCases(testCaseResponses)
                .build();
    }

    // ==========================================
    // MAP TEST CASE
    // ==========================================

    private RunTestCaseResponse mapTestCaseResult(
            TestCaseResult result) {

        return RunTestCaseResponse.builder()
                .testCaseId(result.testCaseId())
                .passed(result.passed())
                .input(result.input())
                .expectedOutput(result.expectedOutput())
                .actualOutput(result.actualOutput())
                .error(result.error())
                .executionTime(result.executionTime())
                .build();
    }

    // ==========================================
    // LANGUAGE PARSER
    // ==========================================

    private Submission.ProgrammingLanguage parseLanguage(
            String language) {

        if (language == null || language.isBlank()) {
            throw new IllegalArgumentException(
                    "Language is required"
            );
        }

        String normalized =
                language
                        .trim()
                        .toUpperCase();

        return switch (normalized) {

            case "JAVA" ->
                    Submission.ProgrammingLanguage.JAVA;

            case "CPP", "C++" ->
                    Submission.ProgrammingLanguage.CPP;

            case "PYTHON", "PY" ->
                    Submission.ProgrammingLanguage.PYTHON;

            case "JAVASCRIPT", "JS" ->
                    Submission.ProgrammingLanguage.JAVASCRIPT;

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported language: " + language
                    );
        };
    }
}