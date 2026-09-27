package com.walletmapx.backend.controller;

import com.walletmapx.backend.dto.ApiResponse;
import com.walletmapx.backend.dto.auth.AuthResponse;
import com.walletmapx.backend.dto.auth.LoginRequest;
import com.walletmapx.backend.dto.auth.RegisterRequest;
import com.walletmapx.backend.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(request));
    }
}