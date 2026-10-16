package com.rohit.leetcode.platform.controller;

import com.rohit.leetcode.platform.dto.RecommendationResponse;
import com.rohit.leetcode.platform.service.RecommendationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public List<RecommendationResponse> getRecommendations() {

        return recommendationService.getRecommendations();
    }

    @GetMapping("/topic/{topic}")
    public List<RecommendationResponse> getByTopic(
            @PathVariable String topic
    ) {

        return recommendationService
                .getRecommendationsByTopic(topic);
    }

    @GetMapping("/difficulty/{difficulty}")
    public List<RecommendationResponse> getByDifficulty(
            @PathVariable String difficulty
    ) {

        return recommendationService
                .getRecommendationsByDifficulty(difficulty);
    }
}