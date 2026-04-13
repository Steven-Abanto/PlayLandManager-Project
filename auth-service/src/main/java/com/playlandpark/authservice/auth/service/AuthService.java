package com.playlandpark.authservice.auth.service;

import com.playlandpark.authservice.auth.dto.auth.LoginRequest;
import com.playlandpark.authservice.auth.dto.auth.LoginResponse;
import com.playlandpark.authservice.auth.dto.auth.MeResponse;

public interface AuthService {
    MeResponse me(String username);
    LoginResponse login(LoginRequest request);
    LoginResponse refresh(String refreshToken);
}