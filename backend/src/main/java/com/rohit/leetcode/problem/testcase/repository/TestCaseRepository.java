package com.rohit.leetcode.problem.testcase.repository;

import com.rohit.leetcode.problem.testcase.entity.TestCase;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestCaseRepository
        extends JpaRepository<TestCase, Long> {

    List<TestCase> findByProblemId(
            Long problemId
    );

    List<TestCase> findByProblemIdOrderByIdAsc(
            Long problemId
    );

    List<TestCase> findByProblemIdAndHiddenFalseOrderByIdAsc(
            Long problemId
    );

    long countByProblemId(
            Long problemId
    );

    long countByProblemIdAndHiddenTrue(
            Long problemId
    );

    long countByProblemIdAndHiddenFalse(
            Long problemId
    );

    // =========================================================
    // PLATFORM-WIDE
    // =========================================================

    long countByHiddenTrue();

    long countByHiddenFalse();
}