package com.rohit.leetcode.favorite.repository;

import com.rohit.leetcode.favorite.entity.Favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository
        extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndProblemId(
            Long userId,
            Long problemId
    );

    boolean existsByUserIdAndProblemId(
            Long userId,
            Long problemId
    );

    void deleteByUserIdAndProblemId(
            Long userId,
            Long problemId
    );

    Page<Favorite> findByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

    long countByUserId(Long userId);

    long countByProblemId(Long problemId);
}