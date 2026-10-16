
package com.rohit.leetcode.service;

import com.rohit.leetcode.dto.LoginRequest;
import com.rohit.leetcode.dto.LoginResponse;
import com.rohit.leetcode.dto.RegisterRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse register(RegisterRequest request);
}
