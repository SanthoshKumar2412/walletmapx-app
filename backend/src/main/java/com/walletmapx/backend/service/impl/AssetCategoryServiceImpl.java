package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.assetcategory.AssetCategoryRequest;
import com.walletmapx.backend.dto.assetcategory.AssetCategoryResponse;
import com.walletmapx.backend.entity.AssetCategory;
import com.walletmapx.backend.exception.BadRequestException;
import com.walletmapx.backend.exception.ResourceNotFoundException;
import com.walletmapx.backend.repository.AssetCategoryRepository;
import com.walletmapx.backend.service.AssetCategoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetCategoryServiceImpl
        implements AssetCategoryService {

    private final AssetCategoryRepository assetCategoryRepository;

    @Override
    public AssetCategoryResponse createCategory(
            AssetCategoryRequest request) {

        if (assetCategoryRepository
                .existsByNameIgnoreCase(request.getName())) {

            throw new BadRequestException(
                    "Asset category already exists"
            );
        }

        AssetCategory category = new AssetCategory();

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        AssetCategory savedCategory =
                assetCategoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetCategoryResponse> getAllCategories() {

        return assetCategoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AssetCategoryResponse getCategoryById(
            Long id) {

        AssetCategory category =
                assetCategoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Asset category not found"
                                )
                        );

        return mapToResponse(category);
    }

    @Override
    public AssetCategoryResponse updateCategory(
            Long id,
            AssetCategoryRequest request) {

        AssetCategory category =
                assetCategoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Asset category not found"
                                )
                        );

        if (assetCategoryRepository
                .findByNameIgnoreCase(request.getName())
                .filter(existing ->
                        !existing.getId().equals(id))
                .isPresent()) {

            throw new BadRequestException(
                    "Asset category already exists"
            );
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        AssetCategory updatedCategory =
                assetCategoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {

        AssetCategory category =
                assetCategoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Asset category not found"
                                )
                        );

        assetCategoryRepository.delete(category);
    }

    private AssetCategoryResponse mapToResponse(
            AssetCategory category) {

        return new AssetCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}