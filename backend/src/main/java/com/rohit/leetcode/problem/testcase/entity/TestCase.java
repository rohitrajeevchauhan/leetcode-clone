package com.rohit.leetcode.problem.testcase.entity;

import com.rohit.leetcode.problem.entity.Problem;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "test_cases",
        indexes = {
                @Index(
                        name = "idx_testcase_problem",
                        columnList = "problem_id"
                ),
                @Index(
                        name = "idx_testcase_hidden",
                        columnList = "hidden"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String input;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String expectedOutput;

    @Column(nullable = false)
    @Builder.Default
    private boolean hidden = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "problem_id",
            nullable = false
    )
    private Problem problem;
}