package com.rohit.leetcode.platform.repository;

import com.rohit.leetcode.platform.entity.StudyPlanProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyPlanProblemRepository
        extends JpaRepository<StudyPlanProblem, Long> {

    List<StudyPlanProblem>
    findByStudyPlanIdOrderByProblemOrderAsc(Long studyPlanId);

    Optional<StudyPlanProblem>
    findByStudyPlanIdAndProblemId(
            Long studyPlanId,
            Long problemId
    );

    boolean existsByStudyPlanIdAndProblemId(
            Long studyPlanId,
            Long problemId
    );

    void deleteByStudyPlanIdAndProblemId(
            Long studyPlanId,
            Long problemId
    );

    long countByStudyPlanId(Long studyPlanId);
}