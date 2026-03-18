package com.playlandpark.playlandmanager.service;

import com.playlandpark.playlandmanager.model.dto.auth.LoginRequest;
import com.playlandpark.playlandmanager.model.dto.auth.LoginResponse;
import com.playlandpark.playlandmanager.model.dto.auth.MeResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    MeResponse me(String username);
}