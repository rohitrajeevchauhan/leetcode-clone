package com.rohit.leetcode.platform.repository;

import com.rohit.leetcode.platform.entity.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyPlanRepository
        extends JpaRepository<StudyPlan, Long> {

    List<StudyPlan>
    findByPublicPlanTrueOrderByCreatedAtDesc();

    List<StudyPlan>
    findByCreatorIdOrderByCreatedAtDesc(Long creatorId);

    List<StudyPlan>
    findByCategoryIgnoreCaseOrderByCreatedAtDesc(
            String category
    );

    List<StudyPlan>
    findByDifficultyIgnoreCaseOrderByCreatedAtDesc(
            String difficulty
    );

    long countByCreatorId(Long creatorId);

    long countByPublicPlanTrue();
}