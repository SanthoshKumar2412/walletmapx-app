package com.walletmapx.backend.dto.monthlysnapshot;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MonthlySnapshotRequest {

    @NotNull(message = "Snapshot month is required")
    private LocalDate snapshotMonth;
}