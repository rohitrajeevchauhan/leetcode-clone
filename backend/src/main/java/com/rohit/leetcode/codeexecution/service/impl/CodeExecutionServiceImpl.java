package com.rohit.leetcode.codeexecution.service.impl;

import com.rohit.leetcode.codeexecution.dto.CodeExecutionRequest;
import com.rohit.leetcode.codeexecution.dto.CodeExecutionResponse;
import com.rohit.leetcode.codeexecution.service.CodeExecutionService;
import com.rohit.leetcode.submission.judge.ProcessExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class CodeExecutionServiceImpl implements CodeExecutionService {
    private final ProcessExecutor processExecutor;

    private static final long COMPILE_TIMEOUT_SECONDS = 5;
    private static final long EXECUTION_TIMEOUT_SECONDS = 2;

    @Override
    public CodeExecutionResponse execute(CodeExecutionRequest request) {

        if (request.getLanguage() == null ||
                !request.getLanguage().equalsIgnoreCase("JAVA")) {

            return CodeExecutionResponse.builder()
                    .stdout("")
                    .stderr("Currently only JAVA is supported for local execution.")
                    .compileOutput("")
                    .status("UNSUPPORTED_LANGUAGE")
                    .build();
        }

        Path tempDirectory = null;

        try {
            tempDirectory = Files.createTempDirectory("leetcode-run-");

            Path sourceFile = tempDirectory.resolve("Main.java");

            Files.writeString(
                    sourceFile,
                    request.getSourceCode(),
                    StandardCharsets.UTF_8
            );

            // Compile
            ProcessBuilder compiler =
                    new ProcessBuilder(
                            "javac",
                            "-encoding",
                            "UTF-8",
                            "Main.java"
                    );

            compiler.directory(tempDirectory.toFile());

            ProcessExecutor.ProcessResult compileResult =
                    processExecutor.execute(
                            compiler,
                            "",
                            COMPILE_TIMEOUT_SECONDS
                    );

            if (compileResult.timedOut()) {

                return CodeExecutionResponse.builder()
                        .stdout("")
                        .stderr("")
                        .compileOutput("Compilation timed out")
                        .status("COMPILATION_ERROR")
                        .build();
            }

            if (compileResult.exitCode() != 0) {

                return CodeExecutionResponse.builder()
                        .stdout("")
                        .stderr("")
                        .compileOutput(compileResult.error())
                        .status("COMPILATION_ERROR")
                        .build();
            }

            // Execute
            ProcessBuilder runner =
                    new ProcessBuilder(
                            "java",
                            "-Xmx256m",
                            "-Xss256k",
                            "Main"
                    );

            runner.directory(tempDirectory.toFile());

            String input =
                    request.getStdin() == null
                            ? ""
                            : request.getStdin();

            ProcessExecutor.ProcessResult executionResult =
                    processExecutor.execute(
                            runner,
                            input,
                            EXECUTION_TIMEOUT_SECONDS
                    );

            if (executionResult.timedOut()) {

                return CodeExecutionResponse.builder()
                        .stdout(executionResult.output())
                        .stderr("")
                        .compileOutput("")
                        .status("TIME_LIMIT_EXCEEDED")
                        .build();
            }

            if (executionResult.exitCode() != 0) {

                return CodeExecutionResponse.builder()
                        .stdout(executionResult.output())
                        .stderr(executionResult.error())
                        .compileOutput("")
                        .status("RUNTIME_ERROR")
                        .build();
            }

            return CodeExecutionResponse.builder()
                    .stdout(executionResult.output())
                    .stderr(executionResult.error())
                    .compileOutput("")
                    .status("ACCEPTED")
                    .build();

        } catch (Exception e) {

            return CodeExecutionResponse.builder()
                    .stdout("")
                    .stderr(e.getMessage())
                    .compileOutput("")
                    .status("RUNTIME_ERROR")
                    .build();

        } finally {

            deleteDirectory(tempDirectory);
        }
    }

    private void deleteDirectory(Path directory) {

        if (directory == null) {
            return;
        }

        try (var paths = Files.walk(directory)) {

            paths.sorted(java.util.Comparator.reverseOrder())
                    .forEach(path -> {

                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }

                    });

        } catch (IOException ignored) {
        }
    }
}
