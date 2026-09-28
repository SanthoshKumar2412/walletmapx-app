package com.walletmapx.backend.service;

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

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Single source of truth for the core financial totals
 * (assets, liabilities, income, expenses, investments, net worth).
 *
 * DashboardServiceImpl, MonthlySnapshotServiceImpl and
 * StatisticsServiceImpl all delegate here instead of each
 * re-implementing the same stream/reduce logic. Fix a
 * calculation once, it's fixed everywhere.
 */
@Component
@RequiredArgsConstructor
public class FinancialAggregationService {

    private final AssetRepository assetRepository;
    private final LiabilityRepository liabilityRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final InvestmentRepository investmentRepository;

    // =========================================================
    // POINT-IN-TIME TOTALS (not date-scoped)
    // =========================================================

    public BigDecimal totalAssets(Long userId) {

        return assetRepository
                .findByUserId(userId)
                .stream()
                .map(Asset::getCurrentValue)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalLiabilities(Long userId) {

        return liabilityRepository
                .findByUserId(userId)
                .stream()
                .map(Liability::getOutstandingAmount)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalInvestments(Long userId) {

        return investmentRepository
                .findByUserId(userId)
                .stream()
                .map(Investment::getCurrentValue)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Net Worth = Assets + Investments - Liabilities.
     * Assets and Investments are stored as separate ledgers
     * (see WalletMapX design decision), so both are additive here.
     */
    public BigDecimal netWorth(Long userId) {

        return totalAssets(userId)
                .add(totalInvestments(userId))
                .subtract(totalLiabilities(userId));
    }

    // =========================================================
    // ALL-TIME INCOME / EXPENSES (no date filter)
    // =========================================================

    public BigDecimal totalIncome(Long userId) {

        return incomeRepository
                .findByUserId(userId)
                .stream()
                .map(Income::getAmount)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalExpenses(Long userId) {

        return expenseRepository
                .findByUserId(userId)
                .stream()
                .map(Expense::getAmount)
                .map(this::safeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // =========================================================
    // DATE-SCOPED INCOME / EXPENSES (inclusive range)
    // =========================================================

    public BigDecimal totalIncomeBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate) {

        return incomeRepository
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
    }

    public BigDecimal totalExpensesBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate) {

        return expenseRepository
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
    }

    // =========================================================
    // NULL SAFETY
    // =========================================================

    private BigDecimal safeAmount(BigDecimal amount) {

        return amount != null
                ? amount
                : BigDecimal.ZERO;
    }
}
