package com.walletmapx.backend.service;

import com.walletmapx.backend.repository.AssetRepository;
import com.walletmapx.backend.repository.ExpenseRepository;
import com.walletmapx.backend.repository.IncomeRepository;
import com.walletmapx.backend.repository.InvestmentRepository;
import com.walletmapx.backend.repository.LiabilityRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Single source of truth for financial totals.
 * All sums run in the database (SUM / GROUP BY), not in Java.
 */
@Component
@RequiredArgsConstructor
public class FinancialAggregationService {

    private final AssetRepository assetRepository;
    private final LiabilityRepository liabilityRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final InvestmentRepository investmentRepository;

    // ---------- point-in-time ----------

    public BigDecimal totalAssets(Long userId) {
        return safe(assetRepository.sumCurrentValue(userId));
    }

    public BigDecimal totalLiabilities(Long userId) {
        return safe(liabilityRepository.sumOutstanding(userId));
    }

    public BigDecimal totalInvestments(Long userId) {
        return safe(investmentRepository.sumCurrentValue(userId));
    }

    /** Net Worth = Assets + Investments - Liabilities */
    public BigDecimal netWorth(Long userId) {
        return totalAssets(userId)
                .add(totalInvestments(userId))
                .subtract(totalLiabilities(userId));
    }

    // ---------- all-time ----------

    public BigDecimal totalIncome(Long userId) {
        return safe(incomeRepository.sumAll(userId));
    }

    public BigDecimal totalExpenses(Long userId) {
        return safe(expenseRepository.sumAll(userId));
    }

    // ---------- date range (inclusive) ----------

    public BigDecimal totalIncomeBetween(Long userId, LocalDate start, LocalDate end) {
        return safe(incomeRepository.sumBetween(userId, start, end));
    }

    public BigDecimal totalExpensesBetween(Long userId, LocalDate start, LocalDate end) {
        return safe(expenseRepository.sumBetween(userId, start, end));
    }

    // ---------- category breakdown ----------

    public Map<String, BigDecimal> incomeByCategoryBetween(
            Long userId, LocalDate start, LocalDate end) {
        return toCategoryMap(
                incomeRepository.sumByCategoryBetween(userId, start, end));
    }

    public Map<String, BigDecimal> expenseByCategoryBetween(
            Long userId, LocalDate start, LocalDate end) {
        return toCategoryMap(
                expenseRepository.sumByCategoryBetween(userId, start, end));
    }

    // ---------- month series ----------

    public Map<YearMonth, BigDecimal> incomeByMonth(
            Long userId, LocalDate start, LocalDate end) {
        return toMonthMap(
                incomeRepository.sumByMonthBetween(userId, start, end));
    }

    public Map<YearMonth, BigDecimal> expenseByMonth(
            Long userId, LocalDate start, LocalDate end) {
        return toMonthMap(
                expenseRepository.sumByMonthBetween(userId, start, end));
    }

    // ---------- helpers ----------

    private Map<String, BigDecimal> toCategoryMap(List<Object[]> rows) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], safe((BigDecimal) row[1]));
        }
        return result;
    }

    private Map<YearMonth, BigDecimal> toMonthMap(List<Object[]> rows) {
        Map<YearMonth, BigDecimal> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            YearMonth ym = YearMonth.of(
                    ((Number) row[0]).intValue(),
                    ((Number) row[1]).intValue());
            result.put(ym, safe((BigDecimal) row[2]));
        }
        return result;
    }

    private BigDecimal safe(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }
}