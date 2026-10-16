package com.rohit.leetcode.editorial.entity;

import com.rohit.leetcode.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "editorials",
        indexes = {
                @Index(name = "idx_editorial_problem", columnList = "problem_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Editorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "problem_id",
            nullable = false,
            unique = true
    )
    private Problem problem;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String explanation;

    @Column(columnDefinition = "TEXT")
    private String approach;

    @Column(columnDefinition = "TEXT")
    private String algorithm;

    @Column(columnDefinition = "TEXT")
    private String complexity;

    @Column(columnDefinition = "TEXT")
    private String javaCode;

    @Column(columnDefinition = "TEXT")
    private String cppCode;

    @Column(columnDefinition = "TEXT")
    private String pythonCode;

    @Column(columnDefinition = "TEXT")
    private String javascriptCode;

    @Column(nullable = false)
    @Builder.Default
    private boolean published = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}