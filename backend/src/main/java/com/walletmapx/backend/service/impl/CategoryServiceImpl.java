package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.ApiResponse;
import com.walletmapx.backend.dto.category.CategoryRequest;
import com.walletmapx.backend.dto.category.CategoryResponse;
import com.walletmapx.backend.entity.Category;
import com.walletmapx.backend.exception.ResourceNotFoundException;
import com.walletmapx.backend.repository.CategoryRepository;
import com.walletmapx.backend.service.CategoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public ApiResponse<CategoryResponse> createCategory(
            CategoryRequest request) {

        Category category = new Category();

        category.setName(request.getName());
        category.setType(request.getType());

        Category savedCategory =
                categoryRepository.save(category);

        CategoryResponse response = new CategoryResponse(
                savedCategory.getId(),
                savedCategory.getName(),
                savedCategory.getType()
        );

        return new ApiResponse<>(
                true,
                "Category created successfully",
                response
        );
    }

    @Override
    public ApiResponse<List<CategoryResponse>> getAllCategories() {

        List<CategoryResponse> categories =
                categoryRepository.findAll()
                        .stream()
                        .map(category -> new CategoryResponse(
                                category.getId(),
                                category.getName(),
                                category.getType()
                        ))
                        .toList();

        return new ApiResponse<>(
                true,
                "Categories retrieved successfully",
                categories
        );
    }

    @Override
    public ApiResponse<CategoryResponse> getCategoryById(Long id) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                ));

        CategoryResponse response = new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType()
        );

        return new ApiResponse<>(
                true,
                "Category retrieved successfully",
                response
        );
    }

    @Override
    public ApiResponse<CategoryResponse> updateCategory(
            Long id,
            CategoryRequest request) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                ));

        category.setName(request.getName());
        category.setType(request.getType());

        Category updatedCategory =
                categoryRepository.save(category);

        CategoryResponse response = new CategoryResponse(
                updatedCategory.getId(),
                updatedCategory.getName(),
                updatedCategory.getType()
        );

        return new ApiResponse<>(
                true,
                "Category updated successfully",
                response
        );
    }

    @Override
    public ApiResponse<Void> deleteCategory(Long id) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                ));

        categoryRepository.delete(category);

        return new ApiResponse<>(
                true,
                "Category deleted successfully",
                null
        );
    }
}