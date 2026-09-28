package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.profile.ProfileResponse;
import com.walletmapx.backend.entity.User;
import com.walletmapx.backend.exception.BadRequestException;
import com.walletmapx.backend.exception.ResourceNotFoundException;
import com.walletmapx.backend.repository.UserRepository;
import com.walletmapx.backend.service.ProfileService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    @Value("${app.upload.profile-dir:uploads/profile}")
    private String profileUploadDir;

    // =========================================================
    // GET MY PROFILE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        return mapToResponse(user);
    }

    // =========================================================
    // UPLOAD PROFILE IMAGE
    // =========================================================

    @Override
    public ProfileResponse uploadProfileImage(
            Long userId,
            MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException(
                    "Profile image is required"
            );
        }

        // 5 MB maximum
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException(
                    "Profile image must be less than 5 MB"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null) {
            throw new BadRequestException(
                    "Unable to determine image type"
            );
        }

        if (!isAllowedContentType(contentType)) {
            throw new BadRequestException(
                    "Only JPG, PNG and WEBP images are allowed"
            );
        }

        // Validate actual file signature
        try {
            if (!isValidImageSignature(file, contentType)) {
                throw new BadRequestException(
                        "Invalid image file"
                );
            }
        } catch (IOException exception) {
            throw new BadRequestException(
                    "Unable to validate profile image"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        Path uploadPath = Paths
                .get(profileUploadDir)
                .toAbsolutePath()
                .normalize();

        String extension = getExtension(contentType);

        String fileName =
                UUID.randomUUID() + extension;

        Path newFilePath =
                uploadPath.resolve(fileName).normalize();

        try {

            Files.createDirectories(uploadPath);

            // Save new image
            file.transferTo(newFilePath);

            String oldImageUrl =
                    user.getProfileImageUrl();

            String imageUrl =
                    "/uploads/profile/" + fileName;

            user.setProfileImageUrl(imageUrl);

            try {

                User updatedUser =
                        userRepository.save(user);

                // Delete old image only after DB update succeeds
                deleteOldProfileImage(oldImageUrl);

                return mapToResponse(updatedUser);

            } catch (Exception exception) {

                // DB failed -> remove newly uploaded file
                deleteFileQuietly(newFilePath);

                throw exception;
            }

        } catch (IOException exception) {

            deleteFileQuietly(newFilePath);

            throw new BadRequestException(
                    "Failed to upload profile image"
            );
        }
    }

    // =========================================================
    // CONTENT TYPE VALIDATION
    // =========================================================

    private boolean isAllowedContentType(
            String contentType) {

        return contentType.equalsIgnoreCase("image/jpeg")
                || contentType.equalsIgnoreCase("image/png")
                || contentType.equalsIgnoreCase("image/webp");
    }

    // =========================================================
    // FILE SIGNATURE VALIDATION
    // =========================================================

    private boolean isValidImageSignature(
            MultipartFile file,
            String contentType) throws IOException {

        byte[] bytes = file.getBytes();

        if (bytes.length < 12) {
            return false;
        }

        // JPEG
        if (contentType.equalsIgnoreCase("image/jpeg")) {

            return (bytes[0] & 0xFF) == 0xFF
                    && (bytes[1] & 0xFF) == 0xD8
                    && (bytes[2] & 0xFF) == 0xFF;
        }

        // PNG
        if (contentType.equalsIgnoreCase("image/png")) {

            return (bytes[0] & 0xFF) == 0x89
                    && (bytes[1] & 0xFF) == 0x50
                    && (bytes[2] & 0xFF) == 0x4E
                    && (bytes[3] & 0xFF) == 0x47
                    && (bytes[4] & 0xFF) == 0x0D
                    && (bytes[5] & 0xFF) == 0x0A
                    && (bytes[6] & 0xFF) == 0x1A
                    && (bytes[7] & 0xFF) == 0x0A;
        }

        // WEBP
        if (contentType.equalsIgnoreCase("image/webp")) {

            return bytes[0] == 'R'
                    && bytes[1] == 'I'
                    && bytes[2] == 'F'
                    && bytes[3] == 'F'
                    && bytes[8] == 'W'
                    && bytes[9] == 'E'
                    && bytes[10] == 'B'
                    && bytes[11] == 'P';
        }

        return false;
    }

    // =========================================================
    // FILE EXTENSION
    // =========================================================

    private String getExtension(
            String contentType) {

        return switch (contentType.toLowerCase()) {

            case "image/jpeg" -> ".jpg";

            case "image/png" -> ".png";

            case "image/webp" -> ".webp";

            default -> throw new BadRequestException(
                    "Unsupported image format"
            );
        };
    }

    // =========================================================
    // DELETE OLD PROFILE IMAGE
    // =========================================================

    private void deleteOldProfileImage(
            String oldImageUrl) {

        if (oldImageUrl == null ||
                oldImageUrl.isBlank()) {
            return;
        }

        try {

            String prefix = "/uploads/profile/";

            if (!oldImageUrl.startsWith(prefix)) {
                return;
            }

            String oldFileName =
                    oldImageUrl.substring(prefix.length());

            Path uploadPath = Paths
                    .get(profileUploadDir)
                    .toAbsolutePath()
                    .normalize();

            Path oldFilePath =
                    uploadPath.resolve(oldFileName)
                            .normalize();

            // Prevent path traversal
            if (!oldFilePath.startsWith(uploadPath)) {
                return;
            }

            Files.deleteIfExists(oldFilePath);

        } catch (Exception ignored) {
            // Old image cleanup should not break a successful upload
        }
    }

    // =========================================================
    // DELETE FILE QUIETLY
    // =========================================================

    private void deleteFileQuietly(
            Path filePath) {

        try {

            Files.deleteIfExists(filePath);

        } catch (IOException ignored) {
            // Nothing else to do
        }
    }

    // =========================================================
    // RESPONSE MAPPING
    // =========================================================

    private ProfileResponse mapToResponse(
            User user) {

        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfileImageUrl()
        );
    }
}