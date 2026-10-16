package com.rohit.leetcode.platform.gamification.entity;

import com.rohit.leetcode.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "gamification_achievements",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_gamification_user_achievement",
                columnNames = {"user_id", "achievement_code"}
        ),
        indexes = {
                @Index(name = "idx_gamification_achievement_user", columnList = "user_id"),
                @Index(name = "idx_gamification_achievement_code", columnList = "achievement_code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamificationAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "achievement_code", nullable = false, length = 60)
    private String achievementCode;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private int xpReward;

    @Column(nullable = false, updatable = false)
    private LocalDateTime unlockedAt;

    @PrePersist
    protected void onCreate() {
        unlockedAt = LocalDateTime.now();
    }
}