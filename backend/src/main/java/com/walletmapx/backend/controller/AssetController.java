package com.walletmapx.backend.controller;

import com.walletmapx.backend.dto.asset.AssetRequest;
import com.walletmapx.backend.dto.asset.AssetResponse;
import com.walletmapx.backend.service.AssetService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    public ResponseEntity<AssetResponse> createAsset(
            @Valid @RequestBody AssetRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assetService.createAsset(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<AssetResponse>> getUserAssets(
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                assetService.getUserAssets(userId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetResponse> getAssetById(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                assetService.getAssetById(userId, id)
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<AssetResponse>> getAssetsByCategory(
            @PathVariable Long categoryId,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                assetService.getAssetsByCategory(
                        userId,
                        categoryId
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssetResponse> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                assetService.updateAsset(
                        userId,
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        assetService.deleteAsset(userId, id);

        return ResponseEntity.noContent().build();
    }

    private Long getUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}