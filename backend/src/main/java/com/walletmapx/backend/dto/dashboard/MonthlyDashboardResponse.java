package com.walletmapx.backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyDashboardResponse {

    private String month;

    private BigDecimal totalIncome;

    private BigDecimal totalExpenses;

    private BigDecimal savings;

    private BigDecimal totalInvestments;
}