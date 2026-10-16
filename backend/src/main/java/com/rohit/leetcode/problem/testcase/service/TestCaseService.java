package com.rohit.leetcode.problem.testcase.service;

import com.rohit.leetcode.problem.testcase.dto.TestCaseRequest;
import com.rohit.leetcode.problem.testcase.dto.TestCaseResponse;

import java.util.List;

public interface TestCaseService {

    TestCaseResponse createTestCase(
            TestCaseRequest request
    );

    TestCaseResponse getTestCaseById(
            Long id
    );

    List<TestCaseResponse> getProblemTestCases(
            Long problemId
    );

    List<TestCaseResponse> getPublicTestCases(
            Long problemId
    );

    TestCaseResponse updateTestCase(
            Long id,
            TestCaseRequest request
    );

    void deleteTestCase(
            Long id
    );

    long countProblemTestCases(
            Long problemId
    );

    long countHiddenTestCases(
            Long problemId
    );

    long countPublicTestCases(
            Long problemId
    );
}