package com.rohit.leetcode.security;

import com.rohit.leetcode.user.entity.User;
import com.rohit.leetcode.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    // =========================================================
    // LOAD USER BY EMAIL
    // =========================================================

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + email
                        )
                );

        // =====================================================
        // CONVERT YOUR USER ENTITY TO SPRING USERDETAILS
        // =====================================================

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())

                /*
                 * If:
                 *
                 * USER  -> ROLE_USER
                 * ADMIN -> ROLE_ADMIN
                 *
                 * .roles() automatically adds ROLE_.
                 */
                .roles(user.getRole().name())

                /*
                 * Your User entity uses:
                 *
                 * active = true
                 * active = false
                 */
                .disabled(!user.isActive())

                .build();
    }
}