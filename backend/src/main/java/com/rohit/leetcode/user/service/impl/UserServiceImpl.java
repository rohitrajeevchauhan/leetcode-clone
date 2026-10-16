package com.rohit.leetcode.user.service.impl;

import com.rohit.leetcode.exception.ResourceNotFoundException;
import com.rohit.leetcode.user.dto.UserCreateRequest;
import com.rohit.leetcode.user.dto.UserResponse;
import com.rohit.leetcode.user.dto.UserUpdateRequest;
import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;
import com.rohit.leetcode.user.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    // =========================================================
    // CREATE USER
    // =========================================================

    @Override
    public UserResponse createUser(UserCreateRequest request) {

        // Check username
        if (userRepository.existsByUsername(request.getUsername())) {

            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }


        // Check email
        if (userRepository.existsByEmail(request.getEmail())) {

            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }


        // Create user
        User user = User.builder()

                .username(request.getUsername())

                .email(request.getEmail())

                /*
                 * NEVER store the raw password.
                 *
                 * Example:
                 *
                 * rohit123
                 *
                 * becomes something like:
                 *
                 * $2a$10$......
                 */
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )

                .role(User.Role.USER)

                .active(true)

                .build();


        User savedUser =
                userRepository.save(user);


        return convertToResponse(savedUser);
    }


    // =========================================================
    // GET USER BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = userRepository
                .findById(id)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );


        return convertToResponse(user);
    }


    // =========================================================
    // GET USER BY USERNAME
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(
            String username) {

        User user = userRepository
                .findByUsername(username)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with username: "
                                        + username
                        )
                );


        return convertToResponse(user);
    }


    // =========================================================
    // GET ALL USERS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository
                .findAll()

                .stream()

                .map(this::convertToResponse)

                .toList();
    }


    // =========================================================
    // UPDATE USER
    // =========================================================

    @Override
    public UserResponse updateUser(
            Long id,
            UserUpdateRequest request) {

        User user = userRepository
                .findById(id)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );


        // -----------------------------------------------------
        // UPDATE USERNAME
        // -----------------------------------------------------

        if (request.getUsername() != null
                && !request.getUsername()
                .equals(user.getUsername())) {


            if (userRepository.existsByUsername(
                    request.getUsername())) {

                throw new IllegalArgumentException(
                        "Username already exists"
                );
            }


            user.setUsername(
                    request.getUsername()
            );
        }


        // -----------------------------------------------------
        // UPDATE EMAIL
        // -----------------------------------------------------

        if (request.getEmail() != null
                && !request.getEmail()
                .equals(user.getEmail())) {


            if (userRepository.existsByEmail(
                    request.getEmail())) {

                throw new IllegalArgumentException(
                        "Email already exists"
                );
            }


            user.setEmail(
                    request.getEmail()
            );
        }


        /*
         * Because this method is @Transactional,
         * JPA automatically detects the changes
         * and updates the database.
         */

        return convertToResponse(user);
    }


    // =========================================================
    // DEACTIVATE USER
    // =========================================================

    @Override
    public void deactivateUser(Long id) {

        User user = userRepository
                .findById(id)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );


        user.setActive(false);
    }


    // =========================================================
    // ENTITY -> RESPONSE DTO
    // =========================================================

    private UserResponse convertToResponse(
            User user) {

        return UserResponse.builder()

                .id(user.getId())

                .username(user.getUsername())

                .email(user.getEmail())

                .role(user.getRole())

                .active(user.isActive())

                .createdAt(user.getCreatedAt())

                .updatedAt(user.getUpdatedAt())

                .build();
    }
}