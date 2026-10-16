package com.rohit.leetcode.achievement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "achievements",
        indexes = {
                @Index(name = "idx_achievement_code", columnList = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 100)
    private String icon;

    @Column(nullable = false)
    private Integer xpReward;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}