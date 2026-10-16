package com.rohit.leetcode.platform.repository;

import com.rohit.leetcode.platform.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserActivityRepository
        extends JpaRepository<UserActivity, Long> {

    List<UserActivity>
    findByUserIdOrderByCreatedAtDesc(Long userId);

    List<UserActivity>
    findTop20ByUserIdOrderByCreatedAtDesc(Long userId);

    List<UserActivity>
    findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByUserId(Long userId);

    long countByActivityType(String activityType);

    long countByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}