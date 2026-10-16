package com.rohit.leetcode.problem.controller;

import com.rohit.leetcode.problem.dto.ProblemProgressResponse;
import com.rohit.leetcode.problem.service.ProblemProgressService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems/progress")
@RequiredArgsConstructor
public class ProblemProgressController {

    private final ProblemProgressService problemProgressService;


    // =========================================================
    // ALL PROBLEM PROGRESS
    // =========================================================

    @GetMapping
    public List<ProblemProgressResponse> getMyProblemProgress() {

        return problemProgressService
                .getMyProblemProgress();
    }


    // =========================================================
    // SINGLE PROBLEM PROGRESS
    // =========================================================

    @GetMapping("/{problemId}")
    public ProblemProgressResponse getMyProblemProgress(
            @PathVariable Long problemId) {

        return problemProgressService
                .getMyProblemProgress(problemId);
    }
}