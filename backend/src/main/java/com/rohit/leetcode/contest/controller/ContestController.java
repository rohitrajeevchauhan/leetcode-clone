package com.rohit.leetcode.contest.controller;

import com.rohit.leetcode.contest.dto.*;
import com.rohit.leetcode.contest.service.ContestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;

    @PostMapping
    public ContestResponse createContest(
            @Valid @RequestBody CreateContestRequest request) {

        return contestService.createContest(request);
    }

    @GetMapping
    public List<ContestResponse> getAllContests() {

        return contestService.getAllContests();
    }

    @GetMapping("/{contestId}")
    public ContestResponse getContest(
            @PathVariable Long contestId) {

        return contestService.getContest(contestId);
    }

    @PostMapping("/{contestId}/problems/{problemId}")
    public void addProblem(
            @PathVariable Long contestId,
            @PathVariable Long problemId,
            @RequestParam Integer order,
            @RequestParam Integer points) {

        contestService.addProblem(
                contestId,
                problemId,
                order,
                points
        );
    }

    @GetMapping("/{contestId}/problems")
    public List<ContestProblemResponse>
    getContestProblems(
            @PathVariable Long contestId) {

        return contestService.getContestProblems(
                contestId
        );
    }

    @PostMapping("/{contestId}/register")
    public void register(
            @PathVariable Long contestId) {

        contestService.register(contestId);
    }

    @GetMapping("/{contestId}/registered")
    public boolean isRegistered(
            @PathVariable Long contestId) {

        return contestService.isRegistered(
                contestId
        );
    }
}