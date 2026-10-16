package com.rohit.leetcode.dashboard.controller;

import com.rohit.leetcode.dashboard.dto.DashboardResponse;
import com.rohit.leetcode.dashboard.service.DashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;


    @GetMapping("/me")
    public ResponseEntity<DashboardResponse>
    getMyDashboard() {

        return ResponseEntity.ok(
                dashboardService.getMyDashboard()
        );
    }
}