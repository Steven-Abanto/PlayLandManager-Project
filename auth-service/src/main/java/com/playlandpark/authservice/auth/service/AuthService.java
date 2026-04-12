package com.playlandpark.authservice.auth.service;

import com.playlandpark.authservice.auth.dto.auth.MeResponse;

public interface AuthService {
    MeResponse me(String username);
}