package com.rohit.leetcode.discussion.entity;

import com.rohit.leetcode.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "discussion_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_discussion_user_like",
                        columnNames = {
                                "discussion_id",
                                "user_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_discussion_like_discussion",
                        columnList = "discussion_id"
                ),
                @Index(
                        name = "idx_discussion_like_user",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "discussion_id",
            nullable = false
    )
    private Discussion discussion;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}