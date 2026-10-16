package com.rohit.leetcode.submission.judge;

public record TestCaseResult(

        Long testCaseId,

        boolean passed,

        String input,

        String expectedOutput,

        String actualOutput,

        String error,

        Long executionTime

) {
}