package com.rohit.leetcode.platform.entity;

import com.rohit.leetcode.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "study_plan_problems",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_study_plan_problem",
                        columnNames = {
                                "study_plan_id",
                                "problem_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_study_plan_problem_plan",
                        columnList = "study_plan_id"
                ),
                @Index(
                        name = "idx_study_plan_problem_problem",
                        columnList = "problem_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyPlanProblem {

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
            name = "problem_id",
            nullable = false
    )
    private Problem problem;

    @Column(nullable = false)
    @Builder.Default
    private Integer problemOrder = 0;

    @PrePersist
    protected void onCreate() {

        if (problemOrder == null) {
            problemOrder = 0;
        }
    }
}