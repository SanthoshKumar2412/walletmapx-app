package com.walletmapx.backend.controller;

import com.walletmapx.backend.dto.monthlysnapshot.MonthlySnapshotRequest;
import com.walletmapx.backend.dto.monthlysnapshot.MonthlySnapshotResponse;
import com.walletmapx.backend.service.MonthlySnapshotService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monthly-snapshots")
@RequiredArgsConstructor
public class MonthlySnapshotController {

    private final MonthlySnapshotService monthlySnapshotService;

    @PostMapping
    public ResponseEntity<MonthlySnapshotResponse> createSnapshot(
            @Valid @RequestBody MonthlySnapshotRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        monthlySnapshotService.createSnapshot(
                                userId,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<MonthlySnapshotResponse>> getMySnapshots(
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                monthlySnapshotService.getMySnapshots(userId)
        );
    }

    @GetMapping("/{month}")
    public ResponseEntity<MonthlySnapshotResponse> getSnapshotByMonth(
            @PathVariable String month,
            Authentication authentication) {

        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                monthlySnapshotService.getSnapshotByMonth(
                        userId,
                        month
                )
        );
    }

    private Long getUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}