package com.walletmapx.backend.controller;

import com.walletmapx.backend.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public ApiResponse<String> test() {

        return new ApiResponse<>(
                true,
                "WalletMapX backend is running",
                "API is working"
        );
    }
}