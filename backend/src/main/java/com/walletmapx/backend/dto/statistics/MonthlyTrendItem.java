package com.walletmapx.backend.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * One point in the monthly income/expense trend series.
 * month is formatted as "YYYY-MM".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyTrendItem {

    private String month;

    private BigDecimal totalIncome;

    private BigDecimal totalExpenses;

    private BigDecimal savings;
}
