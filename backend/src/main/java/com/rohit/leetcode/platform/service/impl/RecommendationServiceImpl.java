package com.rohit.leetcode.platform.service;

import com.rohit.leetcode.platform.dto.RecommendationResponse;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationServiceImpl
        implements RecommendationService {

    @Override
    public List<RecommendationResponse> getRecommendations() {

        List<RecommendationResponse> recommendations =
                new ArrayList<>();

        recommendations.add(
                RecommendationResponse.builder()
                        .problemId(1L)
                        .title("Two Sum")
                        .topic("ARRAY")
                        .difficulty("EASY")
                        .reason("Good problem for mastering HashMap")
                        .build()
        );

        recommendations.add(
                RecommendationResponse.builder()
                        .problemId(2L)
                        .title("Add Two Numbers")
                        .topic("LINKED_LIST")
                        .difficulty("MEDIUM")
                        .reason("Good linked list practice")
                        .build()
        );

        recommendations.add(
                RecommendationResponse.builder()
                        .problemId(3L)
                        .title("Longest Substring Without Repeating Characters")
                        .topic("SLIDING_WINDOW")
                        .difficulty("MEDIUM")
                        .reason("Important sliding window pattern")
                        .build()
        );

        return recommendations;
    }

    @Override
    public List<RecommendationResponse> getRecommendationsByTopic(
            String topic
    ) {

        return getRecommendations()
                .stream()
                .filter(recommendation ->
                        recommendation.getTopic()
                                .equalsIgnoreCase(topic)
                )
                .toList();
    }

    @Override
    public List<RecommendationResponse> getRecommendationsByDifficulty(
            String difficulty
    ) {

        return getRecommendations()
                .stream()
                .filter(recommendation ->
                        recommendation.getDifficulty()
                                .equalsIgnoreCase(difficulty)
                )
                .toList();
    }
}