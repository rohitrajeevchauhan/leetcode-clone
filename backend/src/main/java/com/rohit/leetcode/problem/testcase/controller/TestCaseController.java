package com.rohit.leetcode.problem.testcase.controller;

import com.rohit.leetcode.problem.testcase.dto.TestCaseRequest;
import com.rohit.leetcode.problem.testcase.dto.TestCaseResponse;
import com.rohit.leetcode.problem.testcase.service.TestCaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testcases")
@RequiredArgsConstructor
public class TestCaseController {

    private final TestCaseService testCaseService;

    // Create test case
    @PostMapping
    public TestCaseResponse createTestCase(
            @Valid @RequestBody TestCaseRequest request) {

        return testCaseService.createTestCase(request);
    }

    // Get all test cases of a problem
    @GetMapping("/problem/{problemId}")
    public List<TestCaseResponse> getProblemTestCases(
            @PathVariable Long problemId) {

        return testCaseService.getProblemTestCases(problemId);
    }

    // Get only public test cases
    @GetMapping("/problem/{problemId}/public")
    public List<TestCaseResponse> getPublicTestCases(
            @PathVariable Long problemId) {

        return testCaseService.getPublicTestCases(problemId);
    }

    // Delete test case
    @DeleteMapping("/{id}")
    public String deleteTestCase(@PathVariable Long id) {

        testCaseService.deleteTestCase(id);

        return "Test case deleted successfully.";
    }
}