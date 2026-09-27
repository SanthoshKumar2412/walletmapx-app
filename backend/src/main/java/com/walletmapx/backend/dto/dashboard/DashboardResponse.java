package com.walletmapx.backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private BigDecimal totalAssets;

    private BigDecimal totalLiabilities;

    private BigDecimal netWorth;

    private BigDecimal totalIncome;

    private BigDecimal totalExpenses;

    private BigDecimal totalInvestments;
}