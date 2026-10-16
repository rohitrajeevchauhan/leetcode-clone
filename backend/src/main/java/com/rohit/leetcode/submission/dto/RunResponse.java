
package com.rohit.leetcode.submission.dto;

import com.rohit.leetcode.submission.entity.Submission;
import com.rohit.leetcode.submission.judge.TestCaseResult;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RunResponse {

    private Submission.SubmissionStatus status;

    private String output;

    private String errorMessage;

    private Long executionTime;

    private List<TestCaseResult> testCases;
}
