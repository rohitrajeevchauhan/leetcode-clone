package com.rohit.leetcode.platform.entity;

import com.rohit.leetcode.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "daily_challenge_attempts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_daily_challenge_user",
                        columnNames = {
                                "challenge_id",
                                "user_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_daily_attempt_challenge",
                        columnList = "challenge_id"
                ),
                @Index(
                        name = "idx_daily_attempt_user",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyChallengeAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "challenge_id",
            nullable = false
    )
    private DailyChallenge challenge;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completed = false;

    private LocalDateTime completedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {

        if (completed == null) {
            completed = false;
        }

        createdAt = LocalDateTime.now();
    }
}