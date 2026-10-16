package com.rohit.leetcode.submission.judge;

import com.rohit.leetcode.submission.entity.Submission;

import java.util.List;

public record JudgeResult(

        Submission.SubmissionStatus status,

        String output,

        String errorMessage,

        Long executionTime,

        Long memoryUsed,

        List<TestCaseResult> testCases

) {
}