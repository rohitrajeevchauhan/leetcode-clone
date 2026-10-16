package com.rohit.leetcode.submission.judge;

import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
public class ProcessExecutor {

    public ProcessResult execute(
            ProcessBuilder processBuilder,
            String input,
            long timeoutSeconds
    ) throws Exception {

        Process process = processBuilder.start();

        // Send input
        try (BufferedWriter writer =
                     new BufferedWriter(
                             new OutputStreamWriter(
                                     process.getOutputStream(),
                                     StandardCharsets.UTF_8
                             )
                     )) {

            if (input != null) {
                writer.write(input);
            }

            writer.flush();
        }

        long startTime = System.nanoTime();

        boolean finished =
                process.waitFor(
                        timeoutSeconds,
                        TimeUnit.SECONDS
                );

        long executionTime =
                (System.nanoTime() - startTime)
                        / 1_000_000;

        if (!finished) {

            process.destroyForcibly();

            return new ProcessResult(
                    false,
                    true,
                    "",
                    "Time limit exceeded",
                    executionTime,
                    -1
            );
        }

        String output =
                readStream(process.getInputStream());

        String error =
                readStream(process.getErrorStream());

        int exitCode =
                process.exitValue();

        return new ProcessResult(
                true,
                false,
                output,
                error,
                executionTime,
                exitCode
        );
    }

    private String readStream(InputStream stream)
            throws IOException {

        return new String(
                stream.readAllBytes(),
                StandardCharsets.UTF_8
        );
    }

    public record ProcessResult(
            boolean finished,
            boolean timedOut,
            String output,
            String error,
            long executionTime,
            int exitCode
    ) {
    }
}