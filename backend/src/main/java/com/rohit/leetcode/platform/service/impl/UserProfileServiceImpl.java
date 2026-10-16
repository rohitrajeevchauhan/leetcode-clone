package com.rohit.leetcode.platform.service;

import com.rohit.leetcode.platform.dto.UserProfileRequest;
import com.rohit.leetcode.platform.dto.UserProfileResponse;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl
        implements UserProfileService {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getMyProfile() {

        User user = getCurrentUser();

        return mapToResponse(user);
    }

    @Override
    public UserProfileResponse getProfile(
            String username
    ) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found: " + username
                        )
                );

        return mapToResponse(user);
    }

    @Override
    public UserProfileResponse updateProfile(
            UserProfileRequest request
    ) {

        User user = getCurrentUser();

        if (request.getUsername() != null
                && !request.getUsername().isBlank()
                && !request.getUsername()
                .equals(user.getUsername())) {

            if (userRepository.existsByUsername(
                    request.getUsername()
            )) {
                throw new RuntimeException(
                        "Username already exists"
                );
            }

            user.setUsername(
                    request.getUsername()
            );
        }

        if (request.getEmail() != null
                && !request.getEmail().isBlank()
                && !request.getEmail()
                .equals(user.getEmail())) {

            if (userRepository.existsByEmail(
                    request.getEmail()
            )) {
                throw new RuntimeException(
                        "Email already exists"
                );
            }

            user.setEmail(
                    request.getEmail()
            );
        }

        User savedUser =
                userRepository.save(user);

        return mapToResponse(savedUser);
    }

    // =====================================================
    // GET CURRENT USER
    // =====================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {

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

    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private UserProfileResponse mapToResponse(
            User user
    ) {

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}