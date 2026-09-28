package com.walletmapx.backend.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {

    private BigDecimal totalIncome;

    private BigDecimal totalExpenses;

    private BigDecimal totalSavings;

    /** Percentage, e.g. 23.50 means 23.5%. Zero when totalIncome is zero. */
    private BigDecimal savingsRate;

    private BigDecimal totalAssets;

    private BigDecimal totalLiabilities;

    private BigDecimal totalInvestments;

    private BigDecimal netWorth;

    private Map<String, BigDecimal> incomeByCategory;

    private Map<String, BigDecimal> expenseByCategory;
}
