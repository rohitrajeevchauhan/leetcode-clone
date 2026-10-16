package com.rohit.leetcode.platform.entity;

import com.rohit.leetcode.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_activities",
        indexes = {
                @Index(
                        name = "idx_activity_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_activity_type",
                        columnList = "activity_type"
                ),
                @Index(
                        name = "idx_activity_created",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            name = "activity_type",
            nullable = false,
            length = 50
    )
    private String activityType;

    @Column(length = 100)
    private String entityType;

    private Long entityId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}