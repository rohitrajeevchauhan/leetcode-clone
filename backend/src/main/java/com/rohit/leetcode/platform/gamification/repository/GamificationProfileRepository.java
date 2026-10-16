package com.rohit.leetcode.platform.gamification.repository;

import com.rohit.leetcode.platform.gamification.entity.GamificationProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GamificationProfileRepository
        extends JpaRepository<GamificationProfile, Long> {

    Optional<GamificationProfile> findByUserId(Long userId);

    List<GamificationProfile> findTop10ByOrderByTotalXpDesc();
}