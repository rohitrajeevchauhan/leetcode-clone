package com.rohit.leetcode.submission.judge;

import com.rohit.leetcode.submission.entity.Submission;

import java.util.List;

public interface CodeExecutor {

    boolean supports(
            Submission.ProgrammingLanguage language
    );

    JudgeResult execute(
            String code,
            List<com.rohit.leetcode.problem.testcase.entity.TestCase> testCases
    );
}