package com.rohit.leetcode.platform.controller;

import com.rohit.leetcode.platform.dto.StudyPlanResponse;
import com.rohit.leetcode.platform.service.StudyPlanService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study-plans")
@RequiredArgsConstructor
public class StudyPlanController {

    private final StudyPlanService studyPlanService;

    @GetMapping
    public List<StudyPlanResponse> getAllPlans() {

        return studyPlanService.getAllPlans();
    }

    @GetMapping("/{id}")
    public StudyPlanResponse getPlan(
            @PathVariable Long id
    ) {

        return studyPlanService.getPlanById(id);
    }

    @PostMapping
    public StudyPlanResponse createPlan(
            @RequestBody StudyPlanResponse request
    ) {

        return studyPlanService.createPlan(request);
    }

    @PutMapping("/{id}")
    public StudyPlanResponse updatePlan(
            @PathVariable Long id,
            @RequestBody StudyPlanResponse request
    ) {

        return studyPlanService.updatePlan(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    public void deletePlan(
            @PathVariable Long id
    ) {

        studyPlanService.deletePlan(id);
    }
}