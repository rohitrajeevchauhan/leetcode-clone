package com.rohit.leetcode.progress.controller;

import com.rohit.leetcode.progress.dto.UserProgressResponse;
import com.rohit.leetcode.progress.service.ProgressService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;


    // =========================================================
    // MY COMPLETE PROGRESS
    // =========================================================

    @GetMapping
    public ResponseEntity<UserProgressResponse> getMyProgress() {

        return ResponseEntity.ok(
                progressService.getMyProgress()
        );
    }
}