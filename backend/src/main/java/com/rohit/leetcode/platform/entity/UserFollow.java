package com.rohit.leetcode.platform.entity;

import com.rohit.leetcode.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_follows",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_follower_following",
                        columnNames = {
                                "follower_id",
                                "following_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_follow_follower",
                        columnList = "follower_id"
                ),
                @Index(
                        name = "idx_follow_following",
                        columnList = "following_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "follower_id",
            nullable = false
    )
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "following_id",
            nullable = false
    )
    private User following;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}