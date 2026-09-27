
package com.walletmapx.backend.controller;

import com.walletmapx.backend.dto.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<String>> protectedEndpoint() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "JWT authentication successful",
                        "Welcome to the protected WalletMapX API"
                )
        );
    }
}

