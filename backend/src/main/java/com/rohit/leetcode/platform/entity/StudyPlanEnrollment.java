package com.rohit.leetcode.platform.entity;

import com.rohit.leetcode.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "study_plan_enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_study_plan_enrollment",
                        columnNames = {
                                "study_plan_id",
                                "user_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_enrollment_plan",
                        columnList = "study_plan_id"
                ),
                @Index(
                        name = "idx_enrollment_user",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyPlanEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "study_plan_id",
            nullable = false
    )
    private StudyPlan studyPlan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private Integer completedProblems = 0;

    @Column(nullable = false)
    @Builder.Default
    private Double progressPercentage = 0.0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime enrolledAt;

    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {

        if (completedProblems == null) {
            completedProblems = 0;
        }

        if (progressPercentage == null) {
            progressPercentage = 0.0;
        }

        enrolledAt = LocalDateTime.now();
    }
}