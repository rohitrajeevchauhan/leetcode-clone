package com.rohit.leetcode.platform.gamification.entity;

import com.rohit.leetcode.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "gamification_profiles",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_gamification_profile_user",
                columnNames = "user_id"
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamificationProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private long totalXp = 0;

    @Column(nullable = false)
    @Builder.Default
    private int problemsSolved = 0;

    @Column(nullable = false)
    @Builder.Default
    private int contestsCompleted = 0;

    @Column(nullable = false)
    @Builder.Default
    private int correctQuizAnswers = 0;

    @Column(nullable = false)
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

    public int getLevel() {
        return (int) (Math.max(0, totalXp) / 100) + 1;
    }

    public int getLevelProgress() {
        return (int) (Math.max(0, totalXp) % 100);
    }
}