
package com.rohit.leetcode.submission.service;

import com.rohit.leetcode.submission.dto.RunResponse;
import com.rohit.leetcode.submission.dto.SubmissionRequest;
import com.rohit.leetcode.submission.dto.SubmissionResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubmissionService {

    // Run against public test cases without creating a submission record
    RunResponse runCode(SubmissionRequest request);

    // Submit against all test cases and save the submission
    SubmissionResponse submit(SubmissionRequest request);

    // Get a submission by ID
    SubmissionResponse getSubmissionById(Long id);

    // Get current user's submission history
    Page<SubmissionResponse> getMySubmissions(Pageable pageable);

    // Get current user's submissions for a problem
    Page<SubmissionResponse> getMyProblemSubmissions(
            Long problemId,
            Pageable pageable
    );
}
