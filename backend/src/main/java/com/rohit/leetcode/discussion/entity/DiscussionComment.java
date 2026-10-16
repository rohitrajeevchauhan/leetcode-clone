package com.rohit.leetcode.discussion.entity;

import com.rohit.leetcode.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "discussion_comments",
        indexes = {
                @Index(
                        name = "idx_comment_discussion",
                        columnList = "discussion_id"
                ),
                @Index(
                        name = "idx_comment_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_comment_likes",
                        columnList = "likes"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscussionComment {

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
            columnDefinition = "TEXT"
    )
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private Integer likes = 0;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {

        if (likes == null) {
            likes = 0;
        }

        createdAt = LocalDateTime.now();
    }
}