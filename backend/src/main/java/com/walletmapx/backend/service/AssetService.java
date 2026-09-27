package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.asset.AssetRequest;
import com.walletmapx.backend.dto.asset.AssetResponse;

import java.util.List;

public interface AssetService {

    AssetResponse createAsset(
            Long userId,
            AssetRequest request
    );

    List<AssetResponse> getUserAssets(
            Long userId
    );

    AssetResponse getAssetById(
            Long userId,
            Long assetId
    );

    List<AssetResponse> getAssetsByCategory(
            Long userId,
            Long categoryId
    );

    AssetResponse updateAsset(
            Long userId,
            Long assetId,
            AssetRequest request
    );

    void deleteAsset(
            Long userId,
            Long assetId
    );
}