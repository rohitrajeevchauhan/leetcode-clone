package com.rohit.leetcode.problem.controller;

import com.rohit.leetcode.problem.dto.ProblemRequest;
import com.rohit.leetcode.problem.dto.ProblemResponse;
import com.rohit.leetcode.problem.service.ProblemService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    // =========================================================
    // CREATE PROBLEM
    // =========================================================

    @PostMapping
    public ProblemResponse createProblem(
            @RequestBody ProblemRequest request) {

        return problemService.createProblem(request);
    }

    // =========================================================
    // GET ALL PROBLEMS
    // =========================================================

    @GetMapping
    public List<ProblemResponse> getAllProblems() {

        return problemService.getAllProblems();
    }

    // =========================================================
    // GET PROBLEM BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ProblemResponse getProblemById(
            @PathVariable Long id) {

        return problemService.getProblemById(id);
    }

    // =========================================================
    // UPDATE PROBLEM
    // =========================================================

    @PutMapping("/{id}")
    public ProblemResponse updateProblem(
            @PathVariable Long id,
            @RequestBody ProblemRequest request) {

        return problemService.updateProblem(
                id,
                request
        );
    }

    // =========================================================
    // DELETE PROBLEM
    // =========================================================

    @DeleteMapping("/{id}")
    public void deleteProblem(
            @PathVariable Long id) {

        problemService.deleteProblem(id);
    }
}