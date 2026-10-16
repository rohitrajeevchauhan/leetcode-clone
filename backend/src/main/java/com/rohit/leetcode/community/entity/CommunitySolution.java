package com.rohit.leetcode.community.entity;

import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "community_solutions",
        indexes = {
                @Index(name = "idx_community_solution_problem", columnList = "problem_id"),
                @Index(name = "idx_community_solution_user", columnList = "user_id"),
                @Index(name = "idx_community_solution_language", columnList = "language"),
                @Index(name = "idx_community_solution_created", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunitySolution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProgrammingLanguage language;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(length = 100)
    private String timeComplexity;

    @Column(length = 100)
    private String spaceComplexity;

    @Column(nullable = false)
    @Builder.Default
    private Integer likes = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean published = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (likes == null) {
            likes = 0;
        }

        if (published == null) {
            published = true;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ProgrammingLanguage {
        JAVA,
        CPP,
        PYTHON,
        JAVASCRIPT
    }
}