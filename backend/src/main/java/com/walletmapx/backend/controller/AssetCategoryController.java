package com.walletmapx.backend.controller;

import com.walletmapx.backend.dto.assetcategory.AssetCategoryRequest;
import com.walletmapx.backend.dto.assetcategory.AssetCategoryResponse;
import com.walletmapx.backend.service.AssetCategoryService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset-categories")
@RequiredArgsConstructor
public class AssetCategoryController {

    private final AssetCategoryService assetCategoryService;

    @PostMapping
    public ResponseEntity<AssetCategoryResponse> createCategory(
            @Valid @RequestBody AssetCategoryRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        assetCategoryService.createCategory(request)
                );
    }

    @GetMapping
    public ResponseEntity<List<AssetCategoryResponse>> getAllCategories() {

        return ResponseEntity.ok(
                assetCategoryService.getAllCategories()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetCategoryResponse> getCategoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                assetCategoryService.getCategoryById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssetCategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody AssetCategoryRequest request) {

        return ResponseEntity.ok(
                assetCategoryService.updateCategory(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id) {

        assetCategoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}