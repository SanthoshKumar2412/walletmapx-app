package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.asset.AssetRequest;
import com.walletmapx.backend.dto.asset.AssetResponse;
import com.walletmapx.backend.entity.Asset;
import com.walletmapx.backend.exception.ResourceNotFoundException;
import com.walletmapx.backend.repository.AssetRepository;
import com.walletmapx.backend.service.AssetService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;

    @Override
    public AssetResponse createAsset(
            Long userId,
            AssetRequest request) {

        Asset asset = new Asset();

        asset.setUserId(userId);
        mapRequestToEntity(request, asset);

        Asset savedAsset = assetRepository.save(asset);

        return mapToResponse(savedAsset);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponse> getUserAssets(
            Long userId) {

        return assetRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponse getAssetById(
            Long userId,
            Long assetId) {

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Asset not found"
                        )
                );

        validateOwnership(asset, userId);

        return mapToResponse(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponse> getAssetsByCategory(
            Long userId,
            Long categoryId) {

        return assetRepository
                .findByUserIdAndCategoryId(
                        userId,
                        categoryId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AssetResponse updateAsset(
            Long userId,
            Long assetId,
            AssetRequest request) {

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Asset not found"
                        )
                );

        validateOwnership(asset, userId);

        mapRequestToEntity(request, asset);

        Asset updatedAsset = assetRepository.save(asset);

        return mapToResponse(updatedAsset);
    }

    @Override
    public void deleteAsset(
            Long userId,
            Long assetId) {

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Asset not found"
                        )
                );

        validateOwnership(asset, userId);

        assetRepository.delete(asset);
    }

    private void validateOwnership(
            Asset asset,
            Long userId) {

        if (!asset.getUserId().equals(userId)) {

            /*
             * Return 404 instead of revealing that
             * the record belongs to another user.
             */
            throw new ResourceNotFoundException(
                    "Asset not found"
            );
        }
    }

    private void mapRequestToEntity(
            AssetRequest request,
            Asset asset) {

        asset.setCategoryId(request.getCategoryId());
        asset.setName(request.getName());
        asset.setInstitution(request.getInstitution());
        asset.setInvestedAmount(
                request.getInvestedAmount()
        );
        asset.setCurrentValue(
                request.getCurrentValue()
        );
        asset.setPurchaseDate(
                request.getPurchaseDate()
        );
        asset.setNotes(request.getNotes());
    }

    private AssetResponse mapToResponse(
            Asset asset) {

        return new AssetResponse(
                asset.getId(),
                asset.getUserId(),
                asset.getCategoryId(),
                asset.getName(),
                asset.getInstitution(),
                asset.getInvestedAmount(),
                asset.getCurrentValue(),
                asset.getPurchaseDate(),
                asset.getNotes(),
                asset.getCreatedAt(),
                asset.getUpdatedAt()
        );
    }
}