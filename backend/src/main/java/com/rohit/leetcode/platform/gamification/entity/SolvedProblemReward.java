package com.rohit.leetcode.platform.gamification.entity;

import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "solved_problem_rewards",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reward_user_problem",
                columnNames = {"user_id", "problem_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolvedProblemReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(nullable = false)
    private int xpAwarded;

    @Column(nullable = false, updatable = false)
    private LocalDateTime awardedAt;

    @PrePersist
    protected void onCreate() {
        awardedAt = LocalDateTime.now();
    }
}