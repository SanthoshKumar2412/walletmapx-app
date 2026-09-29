package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.dashboard.DashboardResponse;
import com.walletmapx.backend.dto.dashboard.MonthlyDashboardResponse;
import com.walletmapx.backend.dto.dashboard.YearlyNetWorthItem;
import com.walletmapx.backend.entity.MonthlySnapshot;
import com.walletmapx.backend.exception.BadRequestException;
import com.walletmapx.backend.repository.MonthlySnapshotRepository;
import com.walletmapx.backend.service.DashboardService;
import com.walletmapx.backend.service.FinancialAggregationService;

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


    // =========================================================
    // MAIN DASHBOARD
    // =========================================================

    @Override
    public DashboardResponse getDashboard(Long userId) {

        BigDecimal totalAssets =
                financialAggregationService.totalAssets(userId);

        BigDecimal totalLiabilities =
                financialAggregationService.totalLiabilities(userId);

        BigDecimal totalIncome =
                financialAggregationService.totalIncome(userId);

        BigDecimal totalExpenses =
                financialAggregationService.totalExpenses(userId);

        BigDecimal totalInvestments =
                financialAggregationService.totalInvestments(userId);

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

        YearMonth yearMonth;

        try {
            yearMonth = YearMonth.parse(month);
        } catch (Exception exception) {
            throw new BadRequestException(
                    "Invalid month format. Use YYYY-MM"
            );
        }

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

    @Override
    public List<YearlyNetWorthItem> getYearlyNetWorth(
            Long userId) {

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
    // NULL SAFETY
    // =========================================================

    private BigDecimal safeAmount(
            BigDecimal amount) {

        return amount != null
                ? amount
                : BigDecimal.ZERO;
    }
}