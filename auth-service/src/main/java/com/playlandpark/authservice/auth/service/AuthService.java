package com.playlandpark.authservice.auth.service;

import com.playlandpark.authservice.auth.dto.auth.LoginRequest;
import com.playlandpark.authservice.auth.dto.auth.LoginResponse;
import com.playlandpark.authservice.auth.dto.auth.MeResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    MeResponse me(String username);
}