package com.rohit.leetcode.platform.service.impl;

import com.rohit.leetcode.platform.entity.UserActivity;
import com.rohit.leetcode.platform.repository.UserActivityRepository;
import com.rohit.leetcode.platform.service.ActivityService;
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
public class ActivityServiceImpl
        implements ActivityService {

    private final UserActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Override
    public void recordActivity(
            String activityType,
            String entityType,
            Long entityId,
            String description) {

        User user = getCurrentUser();

        UserActivity activity =
                UserActivity.builder()
                        .user(user)
                        .activityType(activityType)
                        .entityType(entityType)
                        .entityId(entityId)
                        .description(description)
                        .build();

        activityRepository.save(activity);
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