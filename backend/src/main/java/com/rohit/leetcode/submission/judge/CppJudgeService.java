package com.rohit.leetcode.submission.judge;

import com.rohit.leetcode.problem.testcase.entity.TestCase;
import com.rohit.leetcode.submission.entity.Submission;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class CppJudgeService
        extends AbstractCodeExecutor {

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public CppJudgeService(
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
                Submission.ProgrammingLanguage.CPP;
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
                            "leetcode-cpp-"
                    );

            // =================================================
            // CREATE C++ SOURCE FILE
            // =================================================

            Path sourceFile =
                    directory.resolve(
                            "main.cpp"
                    );

            Files.writeString(
                    sourceFile,
                    code,
                    StandardCharsets.UTF_8
            );

            // =================================================
            // COMPILE C++
            // =================================================

            ProcessBuilder compileProcess =
                    new ProcessBuilder(
                            "g++",
                            "-std=c++17",
                            "-O2",
                            "-pipe",
                            "main.cpp",
                            "-o",
                            "main"
                    );

            compileProcess.directory(
                    directory.toFile()
            );

            ProcessExecutor.ProcessResult compileResult =
                    processExecutor.execute(
                            compileProcess,
                            "",
                            COMPILE_TIMEOUT_SECONDS
                    );

            // =================================================
            // COMPILATION TIMEOUT
            // =================================================

            if (compileResult.timedOut()) {

                return new JudgeResult(
                        Submission.SubmissionStatus
                                .TIME_LIMIT_EXCEEDED,
                        "",
                        "Compilation timed out",
                        compileResult.executionTime(),
                        null,
                        List.of()
                );
            }

            // =================================================
            // COMPILATION ERROR
            // =================================================

            if (compileResult.exitCode() != 0) {

                String error =
                        compileResult.error();

                if (error == null ||
                        error.isBlank()) {

                    error =
                            compileResult.output();
                }

                return new JudgeResult(
                        Submission.SubmissionStatus
                                .COMPILATION_ERROR,
                        "",
                        error,
                        compileResult.executionTime(),
                        null,
                        List.of()
                );
            }

            // =================================================
            // RUN TEST CASES
            // =================================================

            return judgeTestCases(
                    directory,

                    workingDirectory -> {

                        ProcessBuilder runner =
                                new ProcessBuilder(
                                        workingDirectory
                                                .resolve("main")
                                                .toAbsolutePath()
                                                .toString()
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
                    Submission.SubmissionStatus.RUNTIME_ERROR,
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