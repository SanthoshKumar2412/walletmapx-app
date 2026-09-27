package com.walletmapx.backend.controller;

import com.walletmapx.backend.dto.liability.LiabilityRequest;
import com.walletmapx.backend.dto.liability.LiabilityResponse;
import com.walletmapx.backend.service.LiabilityService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/liabilities")
@RequiredArgsConstructor
public class LiabilityController {

    private final LiabilityService liabilityService;

    @PostMapping
    public ResponseEntity<LiabilityResponse> createLiability(
            @Valid @RequestBody LiabilityRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        liabilityService.createLiability(
                                userId,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<LiabilityResponse>> getUserLiabilities(
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                liabilityService.getUserLiabilities(userId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<LiabilityResponse> getLiabilityById(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                liabilityService.getLiabilityById(
                        userId,
                        id
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<LiabilityResponse> updateLiability(
            @PathVariable Long id,
            @Valid @RequestBody LiabilityRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                liabilityService.updateLiability(
                        userId,
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLiability(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        liabilityService.deleteLiability(
                userId,
                id
        );

        return ResponseEntity.noContent().build();
    }

    private Long getUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}