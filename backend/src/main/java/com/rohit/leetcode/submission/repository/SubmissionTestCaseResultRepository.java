package com.rohit.leetcode.submission.repository;

import com.rohit.leetcode.submission.entity.SubmissionTestCaseResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionTestCaseResultRepository
        extends JpaRepository<SubmissionTestCaseResult, Long> {

    List<SubmissionTestCaseResult> findBySubmissionIdOrderByIdAsc(
            Long submissionId
    );

    List<SubmissionTestCaseResult> findBySubmissionId(
            Long submissionId
    );

    void deleteBySubmissionId(Long submissionId);
}
