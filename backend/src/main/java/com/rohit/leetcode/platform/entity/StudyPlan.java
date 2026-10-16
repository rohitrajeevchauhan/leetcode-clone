package com.rohit.leetcode.platform.entity;

import com.rohit.leetcode.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "study_plans",
        indexes = {
                @Index(
                        name = "idx_study_plan_creator",
                        columnList = "creator_id"
                ),
                @Index(
                        name = "idx_study_plan_category",
                        columnList = "category"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "creator_id",
            nullable = false
    )
    private User creator;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String category;

    @Column(length = 30)
    private String difficulty;

    private Integer estimatedDays;

    @Column(nullable = false)
    @Builder.Default
    private Boolean publicPlan = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer enrolledUsers = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        if (publicPlan == null) {
            publicPlan = true;
        }

        if (enrolledUsers == null) {
            enrolledUsers = 0;
        }

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}