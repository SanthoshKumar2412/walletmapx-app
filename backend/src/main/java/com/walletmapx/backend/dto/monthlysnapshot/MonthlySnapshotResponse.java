package com.walletmapx.backend.dto.monthlysnapshot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySnapshotResponse {

    private Long id;

    private Long userId;

    private LocalDate snapshotMonth;

    private BigDecimal totalAssets;

    private BigDecimal totalLiabilities;

    private BigDecimal netWorth;

    private BigDecimal totalIncome;

    private BigDecimal totalExpenses;

    private BigDecimal totalInvestments;

    private LocalDateTime createdAt;
}