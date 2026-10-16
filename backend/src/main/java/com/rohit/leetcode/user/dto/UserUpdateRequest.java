package com.rohit.leetcode.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @Size(
            min = 3,
            max = 50,
            message = "Username must be between 3 and 50 characters"
    )
    private String username;

    @Email(message = "Invalid email format")
    @Size(
            max = 100,
            message = "Email cannot exceed 100 characters"
    )
    private String email;
}