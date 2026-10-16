package com.rohit.leetcode.favorite.controller;

import com.rohit.leetcode.favorite.dto.FavoriteProblemResponse;
import com.rohit.leetcode.favorite.dto.FavoriteResponse;
import com.rohit.leetcode.favorite.service.FavoriteService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;


    // =========================================================
    // ADD FAVORITE
    // =========================================================

    @PostMapping("/{problemId}")
    public ResponseEntity<FavoriteResponse> addFavorite(
            @PathVariable Long problemId
    ) {

        return ResponseEntity.ok(
                favoriteService.addFavorite(problemId)
        );
    }


    // =========================================================
    // REMOVE FAVORITE
    // =========================================================

    @DeleteMapping("/{problemId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long problemId
    ) {

        favoriteService.removeFavorite(problemId);

        return ResponseEntity.noContent().build();
    }


    // =========================================================
    // MY FAVORITES
    // =========================================================

    @GetMapping
    public ResponseEntity<Page<FavoriteResponse>> getMyFavorites(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                favoriteService.getMyFavorites(pageable)
        );
    }


    // =========================================================
    // CHECK FAVORITE
    // =========================================================

    @GetMapping("/{problemId}/check")
    public ResponseEntity<FavoriteProblemResponse> checkFavorite(
            @PathVariable Long problemId
    ) {

        return ResponseEntity.ok(
                favoriteService.checkFavorite(problemId)
        );
    }


    // =========================================================
    // MY FAVORITE COUNT
    // =========================================================

    @GetMapping("/count")
    public ResponseEntity<Long> countMyFavorites() {

        return ResponseEntity.ok(
                favoriteService.countMyFavorites()
        );
    }


    // =========================================================
    // PROBLEM FAVORITE COUNT
    // =========================================================

    @GetMapping("/problem/{problemId}/count")
    public ResponseEntity<Long> countProblemFavorites(
            @PathVariable Long problemId
    ) {

        return ResponseEntity.ok(
                favoriteService.countProblemFavorites(problemId)
        );
    }
}