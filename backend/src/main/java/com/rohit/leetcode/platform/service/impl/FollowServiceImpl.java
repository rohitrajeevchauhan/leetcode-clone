package com.rohit.leetcode.platform.service.impl;

import com.rohit.leetcode.platform.entity.UserFollow;
import com.rohit.leetcode.platform.repository.UserFollowRepository;
import com.rohit.leetcode.platform.service.FollowService;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowServiceImpl
        implements FollowService {

    private final UserFollowRepository followRepository;
    private final UserRepository userRepository;

    @Override
    public void follow(String username) {

        User currentUser = getCurrentUser();

        User target =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        if (currentUser.getId().equals(target.getId())) {
            throw new RuntimeException(
                    "You cannot follow yourself"
            );
        }

        if (!followRepository
                .existsByFollowerIdAndFollowingId(
                        currentUser.getId(),
                        target.getId()
                )) {

            UserFollow follow =
                    UserFollow.builder()
                            .follower(currentUser)
                            .following(target)
                            .build();

            followRepository.save(follow);
        }
    }

    @Override
    public void unfollow(String username) {

        User currentUser = getCurrentUser();

        User target =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        followRepository
                .deleteByFollowerIdAndFollowingId(
                        currentUser.getId(),
                        target.getId()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(String username) {

        User currentUser = getCurrentUser();

        User target =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        return followRepository
                .existsByFollowerIdAndFollowingId(
                        currentUser.getId(),
                        target.getId()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public long getFollowersCount(
            String username) {

        User target =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        return followRepository
                .countByFollowingId(target.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public long getFollowingCount(
            String username) {

        User target =
                userRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        return followRepository
                .countByFollowerId(target.getId());
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Current user not found"
                        ));
    }
}