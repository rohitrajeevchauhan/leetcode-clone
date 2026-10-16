package com.rohit.leetcode.contest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "contests",
        indexes = {
                @Index(name = "idx_contest_start_time", columnList = "start_time"),
                @Index(name = "idx_contest_end_time", columnList = "end_time")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ContestStatus status = ContestStatus.UPCOMING;

    @Column(nullable = false)
    @Builder.Default
    private boolean published = false;

    @Column(nullable = false, updatable = false)
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

    public void updateStatus() {

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startTime)) {
            status = ContestStatus.UPCOMING;
        } else if (now.isAfter(endTime)) {
            status = ContestStatus.ENDED;
        } else {
            status = ContestStatus.RUNNING;
        }
    }
}