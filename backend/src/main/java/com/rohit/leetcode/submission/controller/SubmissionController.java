
package com.rohit.leetcode.submission.controller;

import com.rohit.leetcode.submission.dto.RunResponse;
import com.rohit.leetcode.submission.dto.SubmissionRequest;
import com.rohit.leetcode.submission.dto.SubmissionResponse;
import com.rohit.leetcode.submission.service.SubmissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    // Run code against public test cases
    @PostMapping("/run")
    public RunResponse runCode(
            @Valid @RequestBody SubmissionRequest request) {

        return submissionService.runCode(request);
    }

    // Submit code against all test cases
    @PostMapping
    public SubmissionResponse submit(
            @Valid @RequestBody SubmissionRequest request) {

        return submissionService.submit(request);
    }

    // Get my submission history
    @GetMapping
    public Page<SubmissionResponse> getMySubmissions(
            Pageable pageable) {

        return submissionService.getMySubmissions(pageable);
    }

    // Get a submission by ID
    @GetMapping("/{id}")
    public SubmissionResponse getSubmission(
            @PathVariable Long id) {

        return submissionService.getSubmissionById(id);
    }

    // Get my submissions for a particular problem
    @GetMapping("/problem/{problemId}")
    public Page<SubmissionResponse> getProblemSubmissions(
            @PathVariable Long problemId,
            Pageable pageable) {

        return submissionService.getMyProblemSubmissions(
                problemId,
                pageable
        );
    }
}
