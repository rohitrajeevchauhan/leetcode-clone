package com.rohit.leetcode.problem.testcase.service.impl;

import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.problem.repository.ProblemRepository;

import com.rohit.leetcode.problem.testcase.dto.TestCaseRequest;
import com.rohit.leetcode.problem.testcase.dto.TestCaseResponse;

import com.rohit.leetcode.problem.testcase.entity.TestCase;
import com.rohit.leetcode.problem.testcase.repository.TestCaseRepository;
import com.rohit.leetcode.problem.testcase.service.TestCaseService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestCaseServiceImpl
        implements TestCaseService {

    private final TestCaseRepository testCaseRepository;

    private final ProblemRepository problemRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Override
    public TestCaseResponse createTestCase(
            TestCaseRequest request) {

        Problem problem =
                problemRepository.findById(
                        request.getProblemId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Problem not found with id: "
                                        + request.getProblemId()
                        )
                );


        TestCase testCase =
                TestCase.builder()
                        .input(request.getInput())
                        .expectedOutput(
                                request.getExpectedOutput()
                        )
                        .hidden(request.isHidden())
                        .problem(problem)
                        .build();


        TestCase saved =
                testCaseRepository.save(testCase);


        return mapToResponse(saved);
    }


    // =========================================================
    // GET ONE
    // =========================================================

    @Override
    public TestCaseResponse getTestCaseById(
            Long id) {

        TestCase testCase =
                testCaseRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Test case not found with id: "
                                                + id
                                )
                        );


        return mapToResponse(testCase);
    }


    // =========================================================
    // GET ALL TEST CASES FOR PROBLEM
    // ADMIN
    // =========================================================

    @Override
    public List<TestCaseResponse> getProblemTestCases(
            Long problemId) {

        if (!problemRepository.existsById(problemId)) {

            throw new RuntimeException(
                    "Problem not found with id: "
                            + problemId
            );
        }


        return testCaseRepository
                .findByProblemIdOrderByIdAsc(problemId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET PUBLIC TEST CASES
    // USER
    // =========================================================

    @Override
    public List<TestCaseResponse> getPublicTestCases(
            Long problemId) {

        if (!problemRepository.existsById(problemId)) {

            throw new RuntimeException(
                    "Problem not found with id: "
                            + problemId
            );
        }


        return testCaseRepository
                .findByProblemIdAndHiddenFalseOrderByIdAsc(
                        problemId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public TestCaseResponse updateTestCase(
            Long id,
            TestCaseRequest request) {

        TestCase testCase =
                testCaseRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Test case not found with id: "
                                                + id
                                )
                        );


        Problem problem =
                problemRepository.findById(
                        request.getProblemId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Problem not found with id: "
                                        + request.getProblemId()
                        )
                );


        testCase.setInput(
                request.getInput()
        );

        testCase.setExpectedOutput(
                request.getExpectedOutput()
        );

        testCase.setHidden(
                request.isHidden()
        );

        testCase.setProblem(problem);


        TestCase updated =
                testCaseRepository.save(testCase);


        return mapToResponse(updated);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Override
    public void deleteTestCase(
            Long id) {

        if (!testCaseRepository.existsById(id)) {

            throw new RuntimeException(
                    "Test case not found with id: "
                            + id
            );
        }


        testCaseRepository.deleteById(id);
    }


    // =========================================================
    // COUNTS
    // =========================================================

    @Override
    public long countProblemTestCases(
            Long problemId) {

        return testCaseRepository
                .countByProblemId(problemId);
    }


    @Override
    public long countHiddenTestCases(
            Long problemId) {

        return testCaseRepository
                .countByProblemIdAndHiddenTrue(
                        problemId
                );
    }


    @Override
    public long countPublicTestCases(
            Long problemId) {

        return testCaseRepository
                .countByProblemIdAndHiddenFalse(
                        problemId
                );
    }


    // =========================================================
    // MAPPER
    // =========================================================

    private TestCaseResponse mapToResponse(
            TestCase testCase) {

        return TestCaseResponse.builder()

                .id(
                        testCase.getId()
                )

                .problemId(
                        testCase.getProblem().getId()
                )

                .input(
                        testCase.getInput()
                )

                .expectedOutput(
                        testCase.getExpectedOutput()
                )

                .hidden(
                        testCase.isHidden()
                )

                .build();
    }
}