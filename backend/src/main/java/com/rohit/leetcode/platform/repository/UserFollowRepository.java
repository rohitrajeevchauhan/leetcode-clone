package com.rohit.leetcode.platform.repository;

import com.rohit.leetcode.platform.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFollowRepository
        extends JpaRepository<UserFollow, Long> {

    boolean existsByFollowerIdAndFollowingId(
            Long followerId,
            Long followingId
    );

    void deleteByFollowerIdAndFollowingId(
            Long followerId,
            Long followingId
    );

    List<UserFollow>
    findByFollowerIdOrderByCreatedAtDesc(Long followerId);

    List<UserFollow>
    findByFollowingIdOrderByCreatedAtDesc(Long followingId);

    long countByFollowerId(Long followerId);

    long countByFollowingId(Long followingId);
}