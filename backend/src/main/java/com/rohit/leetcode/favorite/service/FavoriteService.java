package com.rohit.leetcode.favorite.service;

import com.rohit.leetcode.favorite.dto.FavoriteProblemResponse;
import com.rohit.leetcode.favorite.dto.FavoriteResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {

    FavoriteResponse addFavorite(Long problemId);

    void removeFavorite(Long problemId);

    Page<FavoriteResponse> getMyFavorites(Pageable pageable);

    FavoriteProblemResponse checkFavorite(Long problemId);

    long countMyFavorites();

    long countProblemFavorites(Long problemId);
}