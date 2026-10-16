package com.rohit.leetcode.platform.service;

import com.rohit.leetcode.platform.dto.StudyPlanResponse;

import java.util.List;

public interface StudyPlanService {

    List<StudyPlanResponse> getAllPlans();

    StudyPlanResponse getPlanById(Long id);

    StudyPlanResponse createPlan(StudyPlanResponse request);

    StudyPlanResponse updatePlan(
            Long id,
            StudyPlanResponse request
    );

    void deletePlan(Long id);
}