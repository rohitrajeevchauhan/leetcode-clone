package com.rohit.leetcode.codeexecution.service.impl;

import com.rohit.leetcode.codeexecution.dto.RunCodeRequest;
import com.rohit.leetcode.codeexecution.dto.RunCodeResponse;
import com.rohit.leetcode.codeexecution.dto.TestCaseResult;
import com.rohit.leetcode.codeexecution.service.RunCodeService;

import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.problem.repository.ProblemRepository;
import com.rohit.leetcode.problem.testcase.entity.TestCase;
import com.rohit.leetcode.problem.testcase.repository.TestCaseRepository;

import com.rohit.leetcode.submission.entity.Submission;
import com.rohit.leetcode.submission.judge.JudgeResult;
import com.rohit.leetcode.submission.judge.MultiLanguageJudgeService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RunCodeServiceImpl implements RunCodeService {

    private final ProblemRepository problemRepository;

    private final TestCaseRepository testCaseRepository;

    private final MultiLanguageJudgeService judgeService;


    // =========================================================
    // RUN CODE
    // =========================================================

    @Override
    public RunCodeResponse run(RunCodeRequest request) {

        // -----------------------------------------------------
        // 1. Validate request
        // -----------------------------------------------------

        if (request == null) {

            throw new IllegalArgumentException(
                    "Run code request cannot be null"
            );
        }

        if (request.getProblemId() == null) {

            throw new IllegalArgumentException(
                    "Problem ID is required"
            );
        }

        if (request.getSourceCode() == null ||
                request.getSourceCode().isBlank()) {

            throw new IllegalArgumentException(
                    "Source code is required"
            );
        }

        if (request.getLanguage() == null ||
                request.getLanguage().isBlank()) {

            throw new IllegalArgumentException(
                    "Programming language is required"
            );
        }


        // -----------------------------------------------------
        // 2. Find problem
        // -----------------------------------------------------

        Problem problem =
                problemRepository.findById(
                        request.getProblemId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Problem not found with id: "
                                        + request.getProblemId()
                        )
                );


        // -----------------------------------------------------
        // 3. Convert language
        // -----------------------------------------------------

        Submission.ProgrammingLanguage language =
                parseLanguage(
                        request.getLanguage()
                );


        // -----------------------------------------------------
        // 4. Get PUBLIC test cases
        //
        // Run Code should NOT expose hidden test cases.
        // -----------------------------------------------------

        List<TestCase> testCases =
                testCaseRepository
                        .findByProblemIdAndHiddenFalseOrderByIdAsc(
                                problem.getId()
                        );


        if (testCases.isEmpty()) {

            return RunCodeResponse.builder()
                    .status("NO_TEST_CASES")
                    .totalTestCases(0)
                    .passedTestCases(0)
                    .results(List.of())
                    .build();
        }


        // -----------------------------------------------------
        // 5. Execute code
        // -----------------------------------------------------

        JudgeResult result =
                judgeService.judge(
                        request.getSourceCode(),
                        language,
                        testCases
                );


        // -----------------------------------------------------
        // 6. Convert judge results
        // -----------------------------------------------------

        List<TestCaseResult> results =
                result.testCases()
                        .stream()
                        .map(this::mapTestCaseResult)
                        .toList();


        // -----------------------------------------------------
        // 7. Count passed test cases
        // -----------------------------------------------------

        int passedTestCases =
                (int) result.testCases()
                        .stream()
                        .filter(
                                com.rohit.leetcode.submission.judge.TestCaseResult::passed
                        )
                        .count();


        // -----------------------------------------------------
        // 8. Return response
        // -----------------------------------------------------

        return RunCodeResponse.builder()
                .status(
                        result.status().name()
                )
                .totalTestCases(
                        results.size()
                )
                .passedTestCases(
                        passedTestCases
                )
                .results(
                        results
                )
                .build();
    }


    // =========================================================
    // LANGUAGE PARSER
    // =========================================================

    private Submission.ProgrammingLanguage parseLanguage(
            String language) {

        String normalized =
                language
                        .trim()
                        .toUpperCase();


        return switch (normalized) {

            case "JAVA" ->
                    Submission.ProgrammingLanguage.JAVA;

            case "CPP",
                 "C++" ->
                    Submission.ProgrammingLanguage.CPP;

            case "PYTHON",
                 "PYTHON3",
                 "PY" ->
                    Submission.ProgrammingLanguage.PYTHON;

            case "JAVASCRIPT",
                 "JS",
                 "NODE",
                 "NODEJS" ->
                    Submission.ProgrammingLanguage.JAVASCRIPT;

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported programming language: "
                                    + language
                    );
        };
    }


    // =========================================================
    // MAP JUDGE RESULT → API RESULT
    // =========================================================

    private TestCaseResult mapTestCaseResult(
            com.rohit.leetcode.submission.judge.TestCaseResult result) {

        return TestCaseResult.builder()
                .testCaseId(
                        result.testCaseId()
                )
                .passed(
                        result.passed()
                )
                .input(
                        result.input()
                )
                .expectedOutput(
                        result.expectedOutput()
                )
                .actualOutput(
                        result.actualOutput()
                )
                .error(
                        result.error()
                )
                .executionTime(
                        result.executionTime()
                )
                .build();
    }
}