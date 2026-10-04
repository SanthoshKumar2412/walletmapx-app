package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.statistics.MonthlyTrendItem;
import com.walletmapx.backend.dto.statistics.StatisticsResponse;
import com.walletmapx.backend.exception.BadRequestException;
import com.walletmapx.backend.service.FinancialAggregationService;
import com.walletmapx.backend.service.StatisticsService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private static final int MAX_TREND_MONTHS = 24;

    private final FinancialAggregationService aggregation;

    // =========================================================
    // OVERVIEW
    // =========================================================

    @Override
    public StatisticsResponse getOverview(Long userId, String month) {

        YearMonth yearMonth = resolveMonth(month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        BigDecimal totalIncome = aggregation.totalIncomeBetween(userId, start, end);
        BigDecimal totalExpenses = aggregation.totalExpensesBetween(userId, start, end);
        BigDecimal totalSavings = totalIncome.subtract(totalExpenses);

        BigDecimal totalAssets = aggregation.totalAssets(userId);
        BigDecimal totalLiabilities = aggregation.totalLiabilities(userId);
        BigDecimal totalInvestments = aggregation.totalInvestments(userId);

        BigDecimal netWorth = totalAssets
                .add(totalInvestments)
                .subtract(totalLiabilities);

        Map<String, BigDecimal> incomeByCategory =
                aggregation.incomeByCategoryBetween(userId, start, end);

        Map<String, BigDecimal> expenseByCategory =
                aggregation.expenseByCategoryBetween(userId, start, end);

        return new StatisticsResponse(
                totalIncome,
                totalExpenses,
                totalSavings,
                calculateSavingsRate(totalIncome, totalSavings),
                totalAssets,
                totalLiabilities,
                totalInvestments,
                netWorth,
                incomeByCategory,
                expenseByCategory
        );
    }

    // =========================================================
    // TREND
    // =========================================================

    @Override
    public List<MonthlyTrendItem> getTrend(Long userId, int months) {

        if (months < 1 || months > MAX_TREND_MONTHS) {
            throw new BadRequestException(
                    "months must be between 1 and " + MAX_TREND_MONTHS);
        }

        YearMonth currentMonth = YearMonth.now();
        YearMonth oldestMonth = currentMonth.minusMonths(months - 1L);

        LocalDate start = oldestMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();

        // Two grouped queries for the whole window
        Map<YearMonth, BigDecimal> incomeByMonth =
                aggregation.incomeByMonth(userId, start, end);

        Map<YearMonth, BigDecimal> expenseByMonth =
                aggregation.expenseByMonth(userId, start, end);

        List<MonthlyTrendItem> trend = new ArrayList<>();

        for (YearMonth cursor = oldestMonth;
             !cursor.isAfter(currentMonth);
             cursor = cursor.plusMonths(1)) {

            BigDecimal income =
                    incomeByMonth.getOrDefault(cursor, BigDecimal.ZERO);
            BigDecimal expense =
                    expenseByMonth.getOrDefault(cursor, BigDecimal.ZERO);

            trend.add(new MonthlyTrendItem(
                    cursor.toString(),
                    income,
                    expense,
                    income.subtract(expense)
            ));
        }

        return trend;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private YearMonth resolveMonth(String month) {

        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }

        try {
            return YearMonth.parse(month);
        } catch (Exception exception) {
            throw new BadRequestException("Invalid month format. Use YYYY-MM");
        }
    }

    private BigDecimal calculateSavingsRate(
            BigDecimal totalIncome,
            BigDecimal totalSavings) {

        if (totalIncome.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalSavings
                .multiply(BigDecimal.valueOf(100))
                .divide(totalIncome, 2, RoundingMode.HALF_UP);
    }
}