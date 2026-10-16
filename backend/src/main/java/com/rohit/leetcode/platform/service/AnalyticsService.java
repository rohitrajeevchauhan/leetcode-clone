package com.rohit.leetcode.platform.service;

import com.rohit.leetcode.platform.dto.UserAnalyticsResponse;

public interface AnalyticsService {

    UserAnalyticsResponse getMyAnalytics();

    UserAnalyticsResponse getOverview();

    UserAnalyticsResponse getActivityAnalytics();

    UserAnalyticsResponse getTopicAnalytics();

    UserAnalyticsResponse getDifficultyAnalytics();
}