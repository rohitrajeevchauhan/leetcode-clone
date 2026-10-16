package com.rohit.leetcode.platform.controller;

import com.rohit.leetcode.platform.dto.AdminAnalyticsResponse;
import com.rohit.leetcode.platform.service.AdminAnalyticsService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin-analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;

    @GetMapping
    public AdminAnalyticsResponse getAnalytics() {

        return adminAnalyticsService.getAnalytics();
    }
}