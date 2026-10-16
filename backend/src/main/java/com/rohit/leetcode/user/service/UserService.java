package com.rohit.leetcode.user.service;

import com.rohit.leetcode.user.dto.UserCreateRequest;
import com.rohit.leetcode.user.dto.UserResponse;
import com.rohit.leetcode.user.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(
            Long id,
            UserUpdateRequest request
    );

    void deactivateUser(Long id);
}