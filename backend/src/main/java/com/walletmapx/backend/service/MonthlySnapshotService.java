package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.monthlysnapshot.MonthlySnapshotRequest;
import com.walletmapx.backend.dto.monthlysnapshot.MonthlySnapshotResponse;

import java.util.List;

public interface MonthlySnapshotService {

    MonthlySnapshotResponse createSnapshot(
            Long userId,
            MonthlySnapshotRequest request
    );

    List<MonthlySnapshotResponse> getMySnapshots(
            Long userId
    );

    MonthlySnapshotResponse getSnapshotByMonth(
            Long userId,
            String month
    );

    /**
     * Creates the snapshot for the current month if it does not
     * exist yet, otherwise refreshes it with the latest totals.
     * Safe to call repeatedly.
     */
    void refreshCurrentMonthSnapshot(Long userId);
}