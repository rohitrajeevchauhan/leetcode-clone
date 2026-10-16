package com.rohit.leetcode.problem.entity;

import com.rohit.leetcode.problem.testcase.entity.TestCase;

import jakarta.persistence.*;

import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "problems",
        indexes = {
                @Index(name = "idx_problem_title", columnList = "title"),
                @Index(name = "idx_problem_difficulty", columnList = "difficulty"),
                @Index(name = "idx_problem_topic", columnList = "topic")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 200
    )
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Difficulty difficulty;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Column(columnDefinition = "TEXT")
    private String example;

    @Column(columnDefinition = "TEXT")
    private String starterCode;

    /*
     * Main topic of the problem.
     *
     * Examples:
     * Array
     * String
     * Linked List
     * Tree
     * Graph
     * Dynamic Programming
     */
    @Column(length = 100)
    private String topic;

    /*
     * Comma-separated tags.
     *
     * Example:
     * array,string,two-pointers
     */
    @Column(length = 500)
    private String tags;

    @Column(columnDefinition = "TEXT")
    private String javaStarterCode;

    @Column(columnDefinition = "TEXT")
    private String cppStarterCode;

    @Column(columnDefinition = "TEXT")
    private String pythonStarterCode;

    @Column(columnDefinition = "TEXT")
    private String javascriptStarterCode;

    @Column(length = 100)
    private String functionName;

    @Column(length = 100)
    private String returnType;

    @Column(columnDefinition = "TEXT")
    private String parameterDefinition;

    @OneToMany(
            mappedBy = "problem",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TestCase> testCases;
}