package com.walletmapx.backend.config;

import lombok.Getter;

@Getter
public class JwtUserPrincipal {

    private final Long userId;
    private final String email;

    public JwtUserPrincipal(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }
}