package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.assetcategory.AssetCategoryRequest;
import com.walletmapx.backend.dto.assetcategory.AssetCategoryResponse;

import java.util.List;

public interface AssetCategoryService {

    AssetCategoryResponse createCategory(
            AssetCategoryRequest request
    );

    List<AssetCategoryResponse> getAllCategories();

    AssetCategoryResponse getCategoryById(
            Long id
    );

    AssetCategoryResponse updateCategory(
            Long id,
            AssetCategoryRequest request
    );

    void deleteCategory(
            Long id
    );
}