package com.walletmapx.backend.controller;

import com.walletmapx.backend.dto.profile.ProfileResponse;
import com.walletmapx.backend.service.ProfileService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // =========================================================
    // GET MY PROFILE
    // =========================================================

    @GetMapping
    public ResponseEntity<ProfileResponse> getMyProfile(
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        return ResponseEntity.ok(
                profileService.getMyProfile(userId)
        );
    }

    // =========================================================
    // UPLOAD PROFILE IMAGE
    // =========================================================

    @PostMapping(
            value = "/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProfileResponse> uploadProfileImage(
            @RequestPart("file") MultipartFile file,
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        return ResponseEntity.ok(
                profileService.uploadProfileImage(
                        userId,
                        file
                )
        );
    }
}