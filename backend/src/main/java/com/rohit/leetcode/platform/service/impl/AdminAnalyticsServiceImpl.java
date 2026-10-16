package com.rohit.leetcode.platform.service;

import com.rohit.leetcode.platform.dto.AdminAnalyticsResponse;
import com.rohit.leetcode.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsServiceImpl
        implements AdminAnalyticsService {

    private final UserRepository userRepository;

    @Override
    public AdminAnalyticsResponse getAnalytics() {

        long totalUsers =
                userRepository.count();

        long activeUsers =
                userRepository.countByActiveTrue();

        long inactiveUsers =
                totalUsers - activeUsers;

        return AdminAnalyticsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .build();
    }
}