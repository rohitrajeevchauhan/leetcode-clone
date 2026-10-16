
package com.rohit.leetcode.submission.judge;

import com.rohit.leetcode.codeexecution.dto.CodeExecutionRequest;
import com.rohit.leetcode.codeexecution.dto.CodeExecutionResponse;
import com.rohit.leetcode.codeexecution.service.CodeExecutionService;
import com.rohit.leetcode.problem.testcase.entity.TestCase;
import com.rohit.leetcode.submission.entity.Submission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JavaCodeExecutor implements CodeExecutor {

    private final CodeExecutionService codeExecutionService;

    @Override
    public boolean supports(Submission.ProgrammingLanguage language) {
        return language == Submission.ProgrammingLanguage.JAVA;
    }

    @Override
    public JudgeResult execute(
            String code,
            List<TestCase> testCases
    ) {
        if (code == null || code.isBlank()) {
            return new JudgeResult(
                    Submission.SubmissionStatus.COMPILATION_ERROR,
                    "",
                    "Source code cannot be empty.",
                    0L,
                    null,
                    List.of()
            );
        }

        if (testCases == null || testCases.isEmpty()) {
            return new JudgeResult(
                    Submission.SubmissionStatus.RUNTIME_ERROR,
                    "",
                    "No test cases available for execution.",
                    0L,
                    null,
                    List.of()
            );
        }

        List<TestCaseResult> results = new ArrayList<>();

        long totalExecutionTime = 0L;
        int passedCount = 0;

        for (TestCase testCase : testCases) {

            CodeExecutionRequest request = CodeExecutionRequest.builder()
                    .sourceCode(code)
                    .language("JAVA")
                    .stdin(
                            testCase.getInput() == null
                                    ? ""
                                    : testCase.getInput()
                    )
                    .build();

            CodeExecutionResponse response;

            try {
                response = codeExecutionService.execute(request);
            } catch (Exception exception) {
                return new JudgeResult(
                        Submission.SubmissionStatus.RUNTIME_ERROR,
                        "",
                        "Code execution failed.",
                        totalExecutionTime,
                        null,
                        results
                );
            }

            if (response == null) {
                return new JudgeResult(
                        Submission.SubmissionStatus.RUNTIME_ERROR,
                        "",
                        "The execution service returned no result.",
                        totalExecutionTime,
                        null,
                        results
                );
            }

            String executionStatus = response.getStatus();

            String actualOutput = normalizeOutput(response.getStdout());

            String expectedOutput = normalizeOutput(
                    testCase.getExpectedOutput()
            );

            boolean passed = "ACCEPTED".equals(executionStatus)
                    && actualOutput.equals(expectedOutput);

            if (passed) {
                passedCount++;
            }

            String error = null;

            if (!"ACCEPTED".equals(executionStatus)) {
                error = firstNonBlank(
                        response.getCompileOutput(),
                        response.getStderr(),
                        executionStatus
                );
            }

            boolean hidden = testCase.isHidden();

            results.add(
                    new TestCaseResult(
                            testCase.getId(),
                            passed,
                            hidden ? null : testCase.getInput(),
                            hidden ? null : testCase.getExpectedOutput(),
                            actualOutput,
                            error,
                            null
                    )
            );

            if ("COMPILATION_ERROR".equals(executionStatus)) {
                return new JudgeResult(
                        Submission.SubmissionStatus.COMPILATION_ERROR,
                        actualOutput,
                        error,
                        totalExecutionTime,
                        null,
                        results
                );
            }

            if ("TIME_LIMIT_EXCEEDED".equals(executionStatus)) {
                return new JudgeResult(
                        Submission.SubmissionStatus.TIME_LIMIT_EXCEEDED,
                        actualOutput,
                        error,
                        totalExecutionTime,
                        null,
                        results
                );
            }

            if ("RUNTIME_ERROR".equals(executionStatus)
                    || "UNSUPPORTED_LANGUAGE".equals(executionStatus)) {
                return new JudgeResult(
                        Submission.SubmissionStatus.RUNTIME_ERROR,
                        actualOutput,
                        error,
                        totalExecutionTime,
                        null,
                        results
                );
            }

            if (!"ACCEPTED".equals(executionStatus)) {
                return new JudgeResult(
                        Submission.SubmissionStatus.RUNTIME_ERROR,
                        actualOutput,
                        error,
                        totalExecutionTime,
                        null,
                        results
                );
            }
        }

        Submission.SubmissionStatus finalStatus =
                passedCount == testCases.size()
                        ? Submission.SubmissionStatus.ACCEPTED
                        : Submission.SubmissionStatus.WRONG_ANSWER;

        return new JudgeResult(
                finalStatus,
                "",
                null,
                totalExecutionTime,
                null,
                results
        );
    }

    private String normalizeOutput(String output) {
        if (output == null) {
            return "";
        }

        return output.trim();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }

        return "Code execution failed.";
    }
}
