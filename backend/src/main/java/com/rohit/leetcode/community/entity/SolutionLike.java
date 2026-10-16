package com.rohit.leetcode.community.entity;

import com.rohit.leetcode.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "solution_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_solution_user_like",
                        columnNames = {"solution_id", "user_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_solution_like_solution",
                        columnList = "solution_id"
                ),
                @Index(
                        name = "idx_solution_like_user",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolutionLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solution_id", nullable = false)
    private CommunitySolution solution;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}