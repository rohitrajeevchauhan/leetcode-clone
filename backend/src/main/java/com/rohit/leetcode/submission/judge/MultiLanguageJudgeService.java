package com.rohit.leetcode.submission.judge;

import com.rohit.leetcode.problem.testcase.entity.TestCase;
import com.rohit.leetcode.submission.entity.Submission;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MultiLanguageJudgeService {

    private final List<CodeExecutor> executors;

    public MultiLanguageJudgeService(
            List<CodeExecutor> executors) {

        this.executors = executors;
    }

    public JudgeResult judge(
            String code,
            Submission.ProgrammingLanguage language,
            List<TestCase> testCases) {

        CodeExecutor executor =
                executors.stream()
                        .filter(
                                item ->
                                        item.supports(language)
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Unsupported language: "
                                                + language
                                )
                        );

        return executor.execute(
                code,
                testCases
        );
    }
}