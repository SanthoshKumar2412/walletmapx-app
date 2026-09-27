package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.dashboard.DashboardResponse;
import com.walletmapx.backend.dto.dashboard.MonthlyDashboardResponse;
import com.walletmapx.backend.entity.Asset;
import com.walletmapx.backend.entity.Expense;
import com.walletmapx.backend.entity.Income;
import com.walletmapx.backend.entity.Investment;
import com.walletmapx.backend.entity.Liability;
import com.walletmapx.backend.repository.AssetRepository;
import com.walletmapx.backend.repository.ExpenseRepository;
import com.walletmapx.backend.repository.IncomeRepository;
import com.walletmapx.backend.repository.InvestmentRepository;
import com.walletmapx.backend.repository.LiabilityRepository;
import com.walletmapx.backend.service.DashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final AssetRepository assetRepository;
    private final LiabilityRepository liabilityRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final InvestmentRepository investmentRepository;

    // =========================================================
    // MAIN DASHBOARD
    // =========================================================

    @Override
    public DashboardResponse getDashboard(Long userId) {

        // =========================
        // TOTAL ASSETS
        // =========================

        BigDecimal totalAssets = assetRepository
                .findByUserId(userId)
                .stream()
                .map(Asset::getCurrentValue)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // =========================
        // TOTAL LIABILITIES
        // =========================

        BigDecimal totalLiabilities = liabilityRepository
                .findByUserId(userId)
                .stream()
                .map(Liability::getOutstandingAmount)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // =========================
        // TOTAL INCOME
        // =========================

        BigDecimal totalIncome = incomeRepository
                .findByUserId(userId)
                .stream()
                .map(Income::getAmount)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // =========================
        // TOTAL EXPENSES
        // =========================

        BigDecimal totalExpenses = expenseRepository
                .findByUserId(userId)
                .stream()
                .map(Expense::getAmount)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // =========================
        // TOTAL INVESTMENTS
        // =========================

        BigDecimal totalInvestments = investmentRepository
                .findByUserId(userId)
                .stream()
                .map(Investment::getCurrentValue)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // =========================
        // NET WORTH
        // =========================
        //
        // Assets and Investments are
        // stored separately.
        //
        // Net Worth =
        // Assets + Investments - Liabilities
        //
        // =========================

        BigDecimal netWorth = totalAssets
                .add(totalInvestments)
                .subtract(totalLiabilities);

        // =========================
        // RESPONSE MAPPING
        // =========================

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

        // Expected format:
        // 2026-09

        YearMonth yearMonth;

        try {
            yearMonth = YearMonth.parse(month);
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Invalid month format. Use YYYY-MM"
            );
        }

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // =========================
        // MONTHLY INCOME
        // =========================

        BigDecimal totalIncome = incomeRepository
                .findByUserId(userId)
                .stream()
                .filter(income ->
                        income.getIncomeDate() != null
                                && !income.getIncomeDate().isBefore(startDate)
                                && !income.getIncomeDate().isAfter(endDate)
                )
                .map(Income::getAmount)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // =========================
        // MONTHLY EXPENSES
        // =========================

        BigDecimal totalExpenses = expenseRepository
                .findByUserId(userId)
                .stream()
                .filter(expense ->
                        expense.getExpenseDate() != null
                                && !expense.getExpenseDate().isBefore(startDate)
                                && !expense.getExpenseDate().isAfter(endDate)
                )
                .map(Expense::getAmount)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // =========================
        // SAVINGS
        // =========================

        BigDecimal savings = totalIncome
                .subtract(totalExpenses);

        // =========================
        // TOTAL INVESTMENTS
        // =========================
        //
        // Investments are not monthly income/expense
        // transactions.
        //
        // So we return the user's current total
        // investment value.
        //
        // =========================

        BigDecimal totalInvestments = investmentRepository
                .findByUserId(userId)
                .stream()
                .map(Investment::getCurrentValue)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // =========================
        // RESPONSE MAPPING
        // =========================

        return new MonthlyDashboardResponse(
                month,
                totalIncome,
                totalExpenses,
                savings,
                totalInvestments
        );
    }

    // =========================================================
    // SAFE BIGDECIMAL MAPPING
    // =========================================================

    private BigDecimal safeAmount(BigDecimal amount) {

        return amount != null
                ? amount
                : BigDecimal.ZERO;
    }
}