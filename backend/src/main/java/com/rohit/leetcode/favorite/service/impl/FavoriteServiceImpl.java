package com.rohit.leetcode.favorite.service.impl;

import com.rohit.leetcode.favorite.dto.FavoriteProblemResponse;
import com.rohit.leetcode.favorite.dto.FavoriteResponse;
import com.rohit.leetcode.favorite.entity.Favorite;
import com.rohit.leetcode.favorite.repository.FavoriteRepository;
import com.rohit.leetcode.favorite.service.FavoriteService;

import com.rohit.leetcode.problem.entity.Problem;
import com.rohit.leetcode.problem.repository.ProblemRepository;

import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final UserRepository userRepository;

    private final ProblemRepository problemRepository;


    // =========================================================
    // ADD FAVORITE
    // =========================================================

    @Override
    public FavoriteResponse addFavorite(Long problemId) {

        User user = getCurrentUser();

        Problem problem =
                problemRepository.findById(problemId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Problem not found with id: "
                                                + problemId
                                )
                        );

        if (favoriteRepository.existsByUserIdAndProblemId(
                user.getId(),
                problemId
        )) {

            throw new IllegalStateException(
                    "Problem is already in favorites"
            );
        }

        Favorite favorite =
                Favorite.builder()
                        .user(user)
                        .problem(problem)
                        .build();

        Favorite saved =
                favoriteRepository.save(favorite);

        return mapToResponse(saved);
    }


    // =========================================================
    // REMOVE FAVORITE
    // =========================================================

    @Override
    public void removeFavorite(Long problemId) {

        User user = getCurrentUser();

        if (!favoriteRepository.existsByUserIdAndProblemId(
                user.getId(),
                problemId
        )) {

            throw new RuntimeException(
                    "Problem is not in your favorites"
            );
        }

        favoriteRepository.deleteByUserIdAndProblemId(
                user.getId(),
                problemId
        );
    }


    // =========================================================
    // GET MY FAVORITES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteResponse> getMyFavorites(
            Pageable pageable
    ) {

        User user = getCurrentUser();

        return favoriteRepository
                .findByUserIdOrderByCreatedAtDesc(
                        user.getId(),
                        pageable
                )
                .map(this::mapToResponse);
    }


    // =========================================================
    // CHECK FAVORITE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public FavoriteProblemResponse checkFavorite(
            Long problemId
    ) {

        User user = getCurrentUser();

        boolean favorite =
                favoriteRepository.existsByUserIdAndProblemId(
                        user.getId(),
                        problemId
                );

        return FavoriteProblemResponse.builder()
                .problemId(problemId)
                .favorite(favorite)
                .build();
    }


    // =========================================================
    // COUNT USER FAVORITES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public long countMyFavorites() {

        User user = getCurrentUser();

        return favoriteRepository.countByUserId(
                user.getId()
        );
    }


    // =========================================================
    // COUNT PROBLEM FAVORITES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public long countProblemFavorites(
            Long problemId
    ) {

        return favoriteRepository.countByProblemId(
                problemId
        );
    }


    // =========================================================
    // CURRENT USER
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"
                        )
                );
    }


    // =========================================================
    // ENTITY → DTO
    // =========================================================

    private FavoriteResponse mapToResponse(
            Favorite favorite
    ) {

        Problem problem =
                favorite.getProblem();

        return FavoriteResponse.builder()
                .favoriteId(favorite.getId())
                .problemId(problem.getId())
                .problemTitle(problem.getTitle())
                .difficulty(
                        problem.getDifficulty() != null
                                ? problem.getDifficulty().name()
                                : null
                )
                .topic(problem.getTopic())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}