package com.rohit.leetcode.platform.service;

import com.rohit.leetcode.platform.dto.StudyPlanResponse;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StudyPlanServiceImpl
        implements StudyPlanService {

    private final List<StudyPlanResponse> plans =
            new ArrayList<>();

    private final AtomicLong idGenerator =
            new AtomicLong(1);

    public StudyPlanServiceImpl() {

        StudyPlanResponse dsa =
                StudyPlanResponse.builder()
                        .id(idGenerator.getAndIncrement())
                        .title("Complete DSA")
                        .description(
                                "Complete Data Structures and Algorithms preparation plan"
                        )
                        .durationDays(90)
                        .difficulty("INTERMEDIATE")
                        .topic("DSA")
                        .build();

        StudyPlanResponse interview =
                StudyPlanResponse.builder()
                        .id(idGenerator.getAndIncrement())
                        .title("Coding Interview Preparation")
                        .description(
                                "Focused preparation for coding interviews"
                        )
                        .durationDays(60)
                        .difficulty("INTERMEDIATE")
                        .topic("INTERVIEW")
                        .build();

        plans.add(dsa);
        plans.add(interview);
    }

    @Override
    public List<StudyPlanResponse> getAllPlans() {
        return new ArrayList<>(plans);
    }

    @Override
    public StudyPlanResponse getPlanById(Long id) {

        return plans.stream()
                .filter(plan ->
                        plan.getId().equals(id)
                )
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Study plan not found with id: " + id
                        )
                );
    }

    @Override
    public StudyPlanResponse createPlan(
            StudyPlanResponse request
    ) {

        request.setId(
                idGenerator.getAndIncrement()
        );

        plans.add(request);

        return request;
    }

    @Override
    public StudyPlanResponse updatePlan(
            Long id,
            StudyPlanResponse request
    ) {

        StudyPlanResponse existing =
                getPlanById(id);

        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setDurationDays(request.getDurationDays());
        existing.setDifficulty(request.getDifficulty());
        existing.setTopic(request.getTopic());

        return existing;
    }

    @Override
    public void deletePlan(Long id) {

        StudyPlanResponse existing =
                getPlanById(id);

        plans.remove(existing);
    }
}