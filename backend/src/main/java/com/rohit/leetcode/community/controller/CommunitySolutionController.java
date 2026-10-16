package com.rohit.leetcode.community.controller;

import com.rohit.leetcode.community.dto.CommunitySolutionRequest;
import com.rohit.leetcode.community.dto.CommunitySolutionResponse;
import com.rohit.leetcode.community.service.CommunitySolutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solutions")
@RequiredArgsConstructor
public class CommunitySolutionController {

    private final CommunitySolutionService solutionService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommunitySolutionResponse createSolution(
            @Valid @RequestBody CommunitySolutionRequest request) {

        return solutionService.createSolution(request);
    }


    @GetMapping("/problem/{problemId}")
    public List<CommunitySolutionResponse> getProblemSolutions(
            @PathVariable Long problemId) {

        return solutionService.getProblemSolutions(
                problemId
        );
    }


    @GetMapping("/problem/{problemId}/top")
    public List<CommunitySolutionResponse> getTopSolutions(
            @PathVariable Long problemId) {

        return solutionService.getTopSolutions(
                problemId
        );
    }


    @GetMapping("/my")
    public List<CommunitySolutionResponse> getMySolutions() {

        return solutionService.getMySolutions();
    }


    @GetMapping("/{solutionId}")
    public CommunitySolutionResponse getSolution(
            @PathVariable Long solutionId) {

        return solutionService.getSolution(
                solutionId
        );
    }


    @DeleteMapping("/{solutionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSolution(
            @PathVariable Long solutionId) {

        solutionService.deleteSolution(
                solutionId
        );
    }
}