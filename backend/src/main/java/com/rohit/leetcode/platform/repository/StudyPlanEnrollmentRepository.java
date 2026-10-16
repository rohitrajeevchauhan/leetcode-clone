package com.rohit.leetcode.platform.repository;

import com.rohit.leetcode.platform.entity.StudyPlanEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyPlanEnrollmentRepository
        extends JpaRepository<StudyPlanEnrollment, Long> {

    Optional<StudyPlanEnrollment>
    findByStudyPlanIdAndUserId(
            Long studyPlanId,
            Long userId
    );

    List<StudyPlanEnrollment>
    findByUserIdOrderByEnrolledAtDesc(Long userId);

    List<StudyPlanEnrollment>
    findByStudyPlanIdOrderByEnrolledAtDesc(Long studyPlanId);

    boolean existsByStudyPlanIdAndUserId(
            Long studyPlanId,
            Long userId
    );

    void deleteByStudyPlanIdAndUserId(
            Long studyPlanId,
            Long userId
    );

    long countByStudyPlanId(Long studyPlanId);

    long countByUserId(Long userId);
}