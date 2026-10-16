package com.rohit.leetcode.achievement.entity;

import com.rohit.leetcode.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_gamification",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_gamification_user",
                        columnNames = "user_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_user_gamification_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_user_gamification_xp",
                        columnList = "total_xp"
                ),
                @Index(
                        name = "idx_user_gamification_level",
                        columnList = "level"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGamification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Column(name = "total_xp", nullable = false)
    @Builder.Default
    private Long totalXp = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Integer level = 1;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentStreak = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer longestStreak = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer problemsSolved = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer contestsParticipated = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer contestsWon = 0;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (totalXp == null) {
            totalXp = 0L;
        }

        if (level == null) {
            level = 1;
        }

        if (currentStreak == null) {
            currentStreak = 0;
        }

        if (longestStreak == null) {
            longestStreak = 0;
        }

        if (problemsSolved == null) {
            problemsSolved = 0;
        }

        if (contestsParticipated == null) {
            contestsParticipated = 0;
        }

        if (contestsWon == null) {
            contestsWon = 0;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}