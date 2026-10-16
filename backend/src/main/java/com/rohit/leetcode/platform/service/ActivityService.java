package com.rohit.leetcode.platform.service;

public interface ActivityService {

    void recordActivity(
            String activityType,
            String entityType,
            Long entityId,
            String description
    );
}