package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.ApiResponse;
import com.walletmapx.backend.dto.category.CategoryRequest;
import com.walletmapx.backend.dto.category.CategoryResponse;

import java.util.List;

public interface CategoryService {

    ApiResponse<CategoryResponse> createCategory(CategoryRequest request);

    ApiResponse<List<CategoryResponse>> getAllCategories();

    ApiResponse<CategoryResponse> getCategoryById(Long id);

    ApiResponse<CategoryResponse> updateCategory(
            Long id,
            CategoryRequest request
    );

    ApiResponse<Void> deleteCategory(Long id);
}

