package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.profile.ProfileResponse;

import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

    ProfileResponse getMyProfile(Long userId);

    ProfileResponse uploadProfileImage(
            Long userId,
            MultipartFile file
    );
}