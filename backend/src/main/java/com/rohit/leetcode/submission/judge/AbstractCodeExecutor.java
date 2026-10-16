package com.rohit.leetcode.submission.judge;

import com.rohit.leetcode.problem.testcase.entity.TestCase;
import com.rohit.leetcode.submission.entity.Submission;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractCodeExecutor
        implements CodeExecutor {

    protected final ProcessExecutor processExecutor;

    protected static final long COMPILE_TIMEOUT_SECONDS = 5;

    protected static final long EXECUTION_TIMEOUT_SECONDS = 2;

    protected String normalize(String output) {

        if (output == null) {
            return "";
        }

        return output
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .trim();
    }

    protected JudgeResult judgeTestCases(
            Path workingDirectory,
            ProcessBuilderFactory factory,
            List<TestCase> testCases) {

        List<TestCaseResult> results =
                new ArrayList<>();

        long totalExecutionTime = 0;

        boolean wrongAnswer = false;
        boolean runtimeError = false;
        boolean timeLimitExceeded = false;

        String firstError = null;
        String lastOutput = "";

        for (TestCase testCase : testCases) {

            try {

                ProcessBuilder processBuilder =
                        factory.create(workingDirectory);

                String input =
                        testCase.getInput() == null
                                ? ""
                                : testCase.getInput();

                ProcessExecutor.ProcessResult result =
                        processExecutor.execute(
                                processBuilder,
                                input,
                                EXECUTION_TIMEOUT_SECONDS
                        );

                totalExecutionTime +=
                        result.executionTime();

                lastOutput =
                        result.output();

                // =============================================
                // TIME LIMIT
                // =============================================

                if (result.timedOut()) {

                    timeLimitExceeded = true;

                    if (firstError == null) {
                        firstError =
                                "Time limit exceeded on test case "
                                        + testCase.getId();
                    }

                    results.add(
                            new TestCaseResult(
                                    testCase.getId(),
                                    false,
                                    testCase.getInput(),
                                    testCase.getExpectedOutput(),
                                    "",
                                    "Time limit exceeded",
                                    result.executionTime()
                            )
                    );

                    continue;
                }

                // =============================================
                // RUNTIME ERROR
                // =============================================

                if (result.exitCode() != 0) {

                    runtimeError = true;

                    if (firstError == null) {
                        firstError =
                                "Runtime error on test case "
                                        + testCase.getId();
                    }

                    results.add(
                            new TestCaseResult(
                                    testCase.getId(),
                                    false,
                                    testCase.getInput(),
                                    testCase.getExpectedOutput(),
                                    result.output(),
                                    result.error(),
                                    result.executionTime()
                            )
                    );

                    continue;
                }

                // =============================================
                // OUTPUT COMPARISON
                // =============================================

                boolean passed =
                        normalize(
                                testCase.getExpectedOutput()
                        )
                                .equals(
                                        normalize(
                                                result.output()
                                        )
                                );

                if (!passed) {

                    wrongAnswer = true;

                    if (firstError == null) {

                        firstError =
                                "Wrong answer on test case "
                                        + testCase.getId();
                    }
                }

                results.add(
                        new TestCaseResult(
                                testCase.getId(),
                                passed,
                                testCase.getInput(),
                                testCase.getExpectedOutput(),
                                result.output(),
                                passed
                                        ? null
                                        : "Wrong answer",
                                result.executionTime()
                        )
                );

            } catch (Exception e) {

                runtimeError = true;

                if (firstError == null) {
                    firstError = e.getMessage();
                }

                results.add(
                        new TestCaseResult(
                                testCase.getId(),
                                false,
                                testCase.getInput(),
                                testCase.getExpectedOutput(),
                                "",
                                e.getMessage(),
                                0L
                        )
                );
            }
        }

        Submission.SubmissionStatus status;

        if (timeLimitExceeded) {

            status =
                    Submission.SubmissionStatus
                            .TIME_LIMIT_EXCEEDED;

        } else if (runtimeError) {

            status =
                    Submission.SubmissionStatus
                            .RUNTIME_ERROR;

        } else if (wrongAnswer) {

            status =
                    Submission.SubmissionStatus
                            .WRONG_ANSWER;

        } else {

            status =
                    Submission.SubmissionStatus
                            .ACCEPTED;
        }

        String finalOutput =
                status ==
                        Submission.SubmissionStatus.ACCEPTED
                        ? "All test cases passed"
                        : lastOutput;

        return new JudgeResult(
                status,
                finalOutput,
                firstError,
                totalExecutionTime,
                null,
                results
        );
    }

    protected void deleteDirectory(
            Path directory) {

        if (directory == null) {
            return;
        }

        try (var paths = Files.walk(directory)) {

            paths
                    .sorted(
                            Comparator.reverseOrder()
                    )
                    .forEach(path -> {

                        try {

                            Files.deleteIfExists(path);

                        } catch (IOException ignored) {
                        }
                    });

        } catch (IOException ignored) {
        }
    }

    @FunctionalInterface
    protected interface ProcessBuilderFactory {

        ProcessBuilder create(
                Path workingDirectory
        );
    }
}