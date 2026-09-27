package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.liability.LiabilityRequest;
import com.walletmapx.backend.dto.liability.LiabilityResponse;

import java.util.List;

public interface LiabilityService {

    LiabilityResponse createLiability(
            Long userId,
            LiabilityRequest request
    );

    List<LiabilityResponse> getUserLiabilities(
            Long userId
    );

    LiabilityResponse getLiabilityById(
            Long userId,
            Long liabilityId
    );

    LiabilityResponse updateLiability(
            Long userId,
            Long liabilityId,
            LiabilityRequest request
    );

    void deleteLiability(
            Long userId,
            Long liabilityId
    );
}