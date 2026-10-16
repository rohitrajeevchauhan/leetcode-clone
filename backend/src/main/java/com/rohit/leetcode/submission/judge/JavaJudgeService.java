package com.rohit.leetcode.submission.judge;

import com.rohit.leetcode.problem.testcase.entity.TestCase;
import com.rohit.leetcode.submission.entity.Submission;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class JavaJudgeService extends AbstractCodeExecutor {

    public JavaJudgeService(
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
                Submission.ProgrammingLanguage.JAVA;
    }

    // =========================================================
    // EXECUTE
    // =========================================================

    @Override
    public JudgeResult execute(
            String sourceCode,
            List<TestCase> testCases) {

        Path workingDirectory = null;

        try {

            // =================================================
            // CREATE TEMPORARY DIRECTORY
            // =================================================

            workingDirectory =
                    Files.createTempDirectory(
                            "leetcode-java-"
                    );

            // =================================================
            // CREATE SOURCE FILE
            // =================================================

            Path sourceFile =
                    workingDirectory.resolve(
                            "Main.java"
                    );

            Files.writeString(
                    sourceFile,
                    sourceCode
            );

            // =================================================
            // COMPILE
            // =================================================

            ProcessBuilder compileProcess =
                    new ProcessBuilder(
                            "javac",
                            "Main.java"
                    );

            compileProcess.directory(
                    workingDirectory.toFile()
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
            // RUN ALL TEST CASES
            // =================================================

            return judgeTestCases(
                    workingDirectory,

                    directory -> {

                        ProcessBuilder processBuilder =
                                new ProcessBuilder(
                                        "java",
                                        "-Xmx256m",
                                        "-Xss256k",
                                        "Main"
                                );

                        processBuilder.directory(
                                directory.toFile()
                        );

                        return processBuilder;
                    },

                    testCases
            );

        } catch (IOException e) {

            return new JudgeResult(
                    Submission.SubmissionStatus
                            .COMPILATION_ERROR,
                    "",
                    e.getMessage(),
                    0L,
                    null,
                    List.of()
            );

        } catch (Exception e) {

            return new JudgeResult(
                    Submission.SubmissionStatus
                            .RUNTIME_ERROR,
                    "",
                    e.getMessage(),
                    0L,
                    null,
                    List.of()
            );

        } finally {

            deleteDirectory(
                    workingDirectory
            );
        }
    }
}