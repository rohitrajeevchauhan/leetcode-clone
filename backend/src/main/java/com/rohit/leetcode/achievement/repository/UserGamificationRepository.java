package com.rohit.leetcode.achievement.repository;

import com.rohit.leetcode.achievement.entity.UserGamification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGamificationRepository
        extends JpaRepository<UserGamification, Long> {

    Optional<UserGamification> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Page<UserGamification> findAllByOrderByTotalXpDesc(
            Pageable pageable
    );

    Page<UserGamification> findAllByOrderByProblemsSolvedDesc(
            Pageable pageable
    );
}