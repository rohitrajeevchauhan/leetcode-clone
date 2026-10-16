package com.rohit.leetcode.platform.service;

import com.rohit.leetcode.platform.dto.RecommendationResponse;

import java.util.List;

public interface RecommendationService {

    List<RecommendationResponse> getRecommendations();

    List<RecommendationResponse> getRecommendationsByTopic(
            String topic
    );

    List<RecommendationResponse> getRecommendationsByDifficulty(
            String difficulty
    );

}