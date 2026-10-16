package com.rohit.leetcode.dashboard.dto;

import com.rohit.leetcode.submission.entity.Submission.SubmissionStatus;

import java.time.LocalDateTime;

public interface DashboardSubmissionProjection {

    Long getSubmissionId();

    Long getProblemId();

    String getProblemTitle();

    String getLanguage();

    SubmissionStatus getStatus();

    Long getExecutionTime();

    LocalDateTime getCreatedAt();
}
