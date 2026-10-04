package com.walletmapx.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        // =========================================================
        // NO TOKEN
        // =========================================================

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authorizationHeader.substring(7);

        // =========================================================
        // JWT VALIDATION
        // =========================================================

        try {

            String email =
                    jwtService.extractEmail(token);

            Long userId =
                    jwtService.extractUserId(token);

            if (email != null &&
                    userId != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                if (jwtService.isTokenValid(token, email)) {

                    UserDetails userDetails =
                            org.springframework.security.core.userdetails.User
                                    .withUsername(String.valueOf(userId))
                                    .password("")
                                    .authorities("USER")
                                    .build();

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (JwtException | IllegalArgumentException e) {

            // Clear any existing authentication
            SecurityContextHolder.clearContext();

            // Return 401 Unauthorized
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired token"
            );

            return;
        }

        // =========================================================
        // CONTINUE REQUEST
        // =========================================================

        filterChain.doFilter(request, response);
    }
}

