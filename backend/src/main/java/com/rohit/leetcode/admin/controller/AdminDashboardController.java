package com.rohit.leetcode.admin.controller;

import com.rohit.leetcode.admin.dto.AdminDashboardResponse;
import com.rohit.leetcode.admin.service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;


    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {

        return adminDashboardService
                .getDashboard();
    }
}