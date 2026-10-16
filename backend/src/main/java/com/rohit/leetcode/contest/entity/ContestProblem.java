package com.rohit.leetcode.contest.entity;

import com.rohit.leetcode.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "contest_problems",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_contest_problem",
                        columnNames = {
                                "contest_id",
                                "problem_id"
                        }
                )
        },
        indexes = {
                @Index(name = "idx_contest_problem_contest", columnList = "contest_id"),
                @Index(name = "idx_contest_problem_problem", columnList = "problem_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(nullable = false)
    private Integer problemOrder;

    @Column(nullable = false)
    @Builder.Default
    private Integer points = 100;
}