package com.rohit.leetcode.user.dto;

import com.rohit.leetcode.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private User.Role role;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}