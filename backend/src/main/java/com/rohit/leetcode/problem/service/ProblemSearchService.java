package com.rohit.leetcode.problem.service;

import com.rohit.leetcode.problem.dto.ProblemSearchResponse;
import com.rohit.leetcode.problem.entity.Difficulty;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemSearchService {

    Page<ProblemSearchResponse> search(
            String keyword,
            Difficulty difficulty,
            String topic,
            String tag,
            String status,
            boolean favoriteOnly,
            Pageable pageable
    );
}