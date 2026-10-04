package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.dashboard.DashboardResponse;
import com.walletmapx.backend.dto.dashboard.MonthlyDashboardResponse;
import com.walletmapx.backend.dto.dashboard.YearlyNetWorthItem;
import com.walletmapx.backend.entity.MonthlySnapshot;
import com.walletmapx.backend.exception.BadRequestException;
import com.walletmapx.backend.repository.MonthlySnapshotRepository;
import com.walletmapx.backend.service.DashboardService;
import com.walletmapx.backend.service.FinancialAggregationService;
import com.walletmapx.backend.service.MonthlySnapshotService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final FinancialAggregationService financialAggregationService;

    private final MonthlySnapshotRepository monthlySnapshotRepository;

    private final MonthlySnapshotService monthlySnapshotService;


    // =========================================================
    // MAIN DASHBOARD
    // =========================================================

    @Override
    public DashboardResponse getDashboard(
            Long userId,
            String month) {

        YearMonth yearMonth = resolveMonth(month);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Point-in-time values (what you own / owe right now)
        BigDecimal totalAssets =
                financialAggregationService.totalAssets(userId);

        BigDecimal totalLiabilities =
                financialAggregationService.totalLiabilities(userId);

        BigDecimal totalInvestments =
                financialAggregationService.totalInvestments(userId);

        // Flow values (money moving in the selected month only)
        BigDecimal totalIncome =
                financialAggregationService.totalIncomeBetween(
                        userId, startDate, endDate
                );

        BigDecimal totalExpenses =
                financialAggregationService.totalExpensesBetween(
                        userId, startDate, endDate
                );

        // Net Worth = Assets + Investments - Liabilities
        BigDecimal netWorth =
                totalAssets
                        .add(totalInvestments)
                        .subtract(totalLiabilities);

        return new DashboardResponse(
                totalAssets,
                totalLiabilities,
                netWorth,
                totalIncome,
                totalExpenses,
                totalInvestments
        );
    }


    // =========================================================
    // MONTHLY DASHBOARD
    // =========================================================

    @Override
    public MonthlyDashboardResponse getMonthlyDashboard(
            Long userId,
            String month) {

        YearMonth yearMonth = resolveMonth(month);

        LocalDate startDate =
                yearMonth.atDay(1);

        LocalDate endDate =
                yearMonth.atEndOfMonth();

        BigDecimal totalIncome =
                financialAggregationService.totalIncomeBetween(
                        userId,
                        startDate,
                        endDate
                );

        BigDecimal totalExpenses =
                financialAggregationService.totalExpensesBetween(
                        userId,
                        startDate,
                        endDate
                );

        BigDecimal savings =
                totalIncome.subtract(totalExpenses);

        BigDecimal totalInvestments =
                financialAggregationService.totalInvestments(userId);

        return new MonthlyDashboardResponse(
                month,
                totalIncome,
                totalExpenses,
                savings,
                totalInvestments
        );
    }


    // =========================================================
    // NET WORTH HISTORY
    // =========================================================

    // Not read-only: keeps the current month's snapshot up to date
    // so the graph always ends at today's real net worth and history
    // builds up automatically month after month.
    @Override
    @Transactional
    public List<YearlyNetWorthItem> getYearlyNetWorth(
            Long userId) {

        monthlySnapshotService.refreshCurrentMonthSnapshot(userId);

        return monthlySnapshotRepository
                .findByUserIdOrderBySnapshotMonthAsc(userId)
                .stream()
                .map(this::mapToYearlyNetWorth)
                .toList();
    }


    // =========================================================
    // MAP SNAPSHOT -> GRAPH ITEM
    // =========================================================

    private YearlyNetWorthItem mapToYearlyNetWorth(
            MonthlySnapshot snapshot) {

        return new YearlyNetWorthItem(
                snapshot.getSnapshotMonth(),
                safeAmount(snapshot.getNetWorth())
        );
    }


    // =========================================================
    // MONTH PARSING (null/blank = current month)
    // =========================================================

    private YearMonth resolveMonth(String month) {

        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }

        try {
            return YearMonth.parse(month);
        } catch (Exception exception) {
            throw new BadRequestException(
                    "Invalid month format. Use YYYY-MM"
            );
        }
    }


    // =========================================================
    // NULL SAFETY
    // =========================================================

    private BigDecimal safeAmount(
            BigDecimal amount) {

        return amount != null
                ? amount
                : BigDecimal.ZERO;
    }
}