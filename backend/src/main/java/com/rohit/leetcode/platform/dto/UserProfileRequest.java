package com.rohit.leetcode.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest {

    @Size(
            min = 3,
            max = 50,
            message = "Username must be between 3 and 50 characters"
    )
    private String username;

    @Email(
            message = "Invalid email address"
    )
    @Size(
            max = 100,
            message = "Email cannot exceed 100 characters"
    )
    private String email;
}