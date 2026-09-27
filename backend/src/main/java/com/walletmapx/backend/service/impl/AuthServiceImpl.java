
package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.config.JwtService;
import com.walletmapx.backend.dto.ApiResponse;
import com.walletmapx.backend.dto.auth.AuthResponse;
import com.walletmapx.backend.dto.auth.LoginRequest;
import com.walletmapx.backend.dto.auth.RegisterRequest;
import com.walletmapx.backend.entity.User;
import com.walletmapx.backend.exception.BadRequestException;
import com.walletmapx.backend.repository.UserRepository;
import com.walletmapx.backend.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public ApiResponse<String> register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        user.setPassword(encodedPassword);

        userRepository.save(user);

        return new ApiResponse<>(
                true,
                "User registered successfully",
                null
        );
    }

    @Override
    public ApiResponse<AuthResponse> login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new BadRequestException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail()
        );

        AuthResponse authResponse = new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        return new ApiResponse<>(
                true,
                "Login successful",
                authResponse
        );
    }
}

