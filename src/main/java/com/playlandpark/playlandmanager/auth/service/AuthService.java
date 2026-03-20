package com.playlandpark.playlandmanager.auth.service;

import com.playlandpark.playlandmanager.auth.dto.auth.LoginRequest;
import com.playlandpark.playlandmanager.auth.dto.auth.LoginResponse;
import com.playlandpark.playlandmanager.auth.dto.auth.MeResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    MeResponse me(String username);
}