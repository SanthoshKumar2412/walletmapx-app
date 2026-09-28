package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.statistics.MonthlyTrendItem;
import com.walletmapx.backend.dto.statistics.StatisticsResponse;
import com.walletmapx.backend.entity.Expense;
import com.walletmapx.backend.entity.Income;
import com.walletmapx.backend.exception.BadRequestException;
import com.walletmapx.backend.repository.ExpenseRepository;
import com.walletmapx.backend.repository.IncomeRepository;
import com.walletmapx.backend.service.FinancialAggregationService;
import com.walletmapx.backend.service.StatisticsService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private static final int MAX_TREND_MONTHS = 24;

    private final FinancialAggregationService financialAggregationService;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    // =========================================================
    // OVERVIEW
    // =========================================================

    @Override
    public StatisticsResponse getOverview(Long userId) {

        BigDecimal totalIncome =
                financialAggregationService.totalIncome(userId);

        BigDecimal totalExpenses =
                financialAggregationService.totalExpenses(userId);

        BigDecimal totalSavings =
                totalIncome.subtract(totalExpenses);

        BigDecimal savingsRate =
                calculateSavingsRate(totalIncome, totalSavings);

        BigDecimal totalAssets =
                financialAggregationService.totalAssets(userId);

        BigDecimal totalLiabilities =
                financialAggregationService.totalLiabilities(userId);

        BigDecimal totalInvestments =
                financialAggregationService.totalInvestments(userId);

        // Net Worth = Assets + Investments - Liabilities
        BigDecimal netWorth = totalAssets
                .add(totalInvestments)
                .subtract(totalLiabilities);

        Map<String, BigDecimal> incomeByCategory =
                incomeRepository.findByUserId(userId)
                        .stream()
                        .collect(Collectors.groupingBy(
                                Income::getCategory,
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        Income::getAmount,
                                        BigDecimal::add
                                )
                        ));

        Map<String, BigDecimal> expenseByCategory =
                expenseRepository.findByUserId(userId)
                        .stream()
                        .collect(Collectors.groupingBy(
                                Expense::getCategory,
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        Expense::getAmount,
                                        BigDecimal::add
                                )
                        ));

        return new StatisticsResponse(
                totalIncome,
                totalExpenses,
                totalSavings,
                savingsRate,
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
                    "months must be between 1 and " + MAX_TREND_MONTHS
            );
        }

        YearMonth currentMonth = YearMonth.now();
        YearMonth oldestMonth = currentMonth.minusMonths(months - 1L);

        // Pull each collection once and group in memory rather
        // than re-querying the DB per month.
        Map<YearMonth, BigDecimal> incomeByMonth = incomeRepository
                .findByUserId(userId)
                .stream()
                .filter(income -> income.getIncomeDate() != null)
                .collect(Collectors.groupingBy(
                        income -> YearMonth.from(income.getIncomeDate()),
                        TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Income::getAmount,
                                BigDecimal::add
                        )
                ));

        Map<YearMonth, BigDecimal> expenseByMonth = expenseRepository
                .findByUserId(userId)
                .stream()
                .filter(expense -> expense.getExpenseDate() != null)
                .collect(Collectors.groupingBy(
                        expense -> YearMonth.from(expense.getExpenseDate()),
                        TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add
                        )
                ));

        List<MonthlyTrendItem> trend = new ArrayList<>();

        for (YearMonth cursor = oldestMonth;
             !cursor.isAfter(currentMonth);
             cursor = cursor.plusMonths(1)) {

            BigDecimal income = incomeByMonth.getOrDefault(
                    cursor,
                    BigDecimal.ZERO
            );

            BigDecimal expense = expenseByMonth.getOrDefault(
                    cursor,
                    BigDecimal.ZERO
            );

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
    // SAVINGS RATE
    // =========================================================

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
