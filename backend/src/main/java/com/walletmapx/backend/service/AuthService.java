package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.ApiResponse;
import com.walletmapx.backend.dto.auth.AuthResponse;
import com.walletmapx.backend.dto.auth.LoginRequest;
import com.walletmapx.backend.dto.auth.RegisterRequest;

public interface AuthService {

    ApiResponse<String> register(RegisterRequest request);

    ApiResponse<AuthResponse> login(LoginRequest request);
}