package com.rohit.leetcode.platform.entity;

import com.rohit.leetcode.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "daily_challenges",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_daily_challenge_date",
                        columnNames = "challenge_date"
                )
        },
        indexes = {
                @Index(
                        name = "idx_daily_challenge_date",
                        columnList = "challenge_date"
                ),
                @Index(
                        name = "idx_daily_challenge_problem",
                        columnList = "problem_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "challenge_date",
            nullable = false,
            unique = true
    )
    private LocalDate challengeDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(nullable = false)
    @Builder.Default
    private Integer xpReward = 50;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {

        if (xpReward == null) {
            xpReward = 50;
        }

        if (active == null) {
            active = true;
        }

        createdAt = LocalDateTime.now();
    }
}