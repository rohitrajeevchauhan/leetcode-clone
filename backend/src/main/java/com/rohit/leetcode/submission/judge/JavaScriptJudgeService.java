package com.rohit.leetcode.submission.judge;

import com.rohit.leetcode.problem.testcase.entity.TestCase;
import com.rohit.leetcode.submission.entity.Submission;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class JavaScriptJudgeService
        extends AbstractCodeExecutor {

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public JavaScriptJudgeService(
            ProcessExecutor processExecutor) {

        super(processExecutor);
    }

    // =========================================================
    // LANGUAGE SUPPORT
    // =========================================================

    @Override
    public boolean supports(
            Submission.ProgrammingLanguage language) {

        return language ==
                Submission.ProgrammingLanguage.JAVASCRIPT;
    }

    // =========================================================
    // EXECUTE
    // =========================================================

    @Override
    public JudgeResult execute(
            String code,
            List<TestCase> testCases) {

        Path directory = null;

        try {

            // =================================================
            // CREATE TEMP DIRECTORY
            // =================================================

            directory =
                    Files.createTempDirectory(
                            "leetcode-javascript-"
                    );

            // =================================================
            // CREATE SOURCE FILE
            // =================================================

            Path sourceFile =
                    directory.resolve(
                            "main.js"
                    );

            Files.writeString(
                    sourceFile,
                    code,
                    StandardCharsets.UTF_8
            );

            // =================================================
            // RUN TEST CASES
            // =================================================

            return judgeTestCases(
                    directory,

                    workingDirectory -> {

                        ProcessBuilder runner =
                                new ProcessBuilder(
                                        "node",
                                        "main.js"
                                );

                        runner.directory(
                                workingDirectory.toFile()
                        );

                        return runner;
                    },

                    testCases
            );

        } catch (Exception e) {

            return new JudgeResult(
                    Submission.SubmissionStatus
                            .RUNTIME_ERROR,
                    "",
                    e.getMessage(),
                    null,
                    null,
                    List.of()
            );

        } finally {

            deleteDirectory(
                    directory
            );
        }
    }
}