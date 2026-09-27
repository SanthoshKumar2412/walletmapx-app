package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.monthlysnapshot.MonthlySnapshotRequest;
import com.walletmapx.backend.dto.monthlysnapshot.MonthlySnapshotResponse;
import com.walletmapx.backend.entity.Asset;
import com.walletmapx.backend.entity.Expense;
import com.walletmapx.backend.entity.Income;
import com.walletmapx.backend.entity.Investment;
import com.walletmapx.backend.entity.Liability;
import com.walletmapx.backend.entity.MonthlySnapshot;
import com.walletmapx.backend.exception.BadRequestException;
import com.walletmapx.backend.exception.ResourceNotFoundException;
import com.walletmapx.backend.repository.AssetRepository;
import com.walletmapx.backend.repository.ExpenseRepository;
import com.walletmapx.backend.repository.IncomeRepository;
import com.walletmapx.backend.repository.InvestmentRepository;
import com.walletmapx.backend.repository.LiabilityRepository;
import com.walletmapx.backend.repository.MonthlySnapshotRepository;
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
@Transactional
public class MonthlySnapshotServiceImpl
        implements MonthlySnapshotService {

    private final MonthlySnapshotRepository monthlySnapshotRepository;

    private final AssetRepository assetRepository;
    private final LiabilityRepository liabilityRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final InvestmentRepository investmentRepository;

    // =========================================================
    // CREATE MONTHLY SNAPSHOT
    // =========================================================

    @Override
    public MonthlySnapshotResponse createSnapshot(
            Long userId,
            MonthlySnapshotRequest request) {

        if (request == null || request.getSnapshotMonth() == null) {
            throw new BadRequestException(
                    "Snapshot month is required"
            );
        }

        /*
         * Always store the first day of the month.
         *
         * Example:
         * 2026-09-27 -> 2026-09-01
         *
         * This keeps the database consistent and allows:
         *
         * findByUserIdAndSnapshotMonth(...)
         *
         * to work correctly.
         */
        YearMonth yearMonth =
                YearMonth.from(request.getSnapshotMonth());

        LocalDate snapshotMonth =
                yearMonth.atDay(1);

        // =====================================================
        // CHECK DUPLICATE SNAPSHOT
        // =====================================================

        if (monthlySnapshotRepository
                .findByUserIdAndSnapshotMonth(
                        userId,
                        snapshotMonth
                )
                .isPresent()) {

            throw new BadRequestException(
                    "Monthly snapshot already exists for "
                            + yearMonth
            );
        }

        // =====================================================
        // MONTH DATE RANGE
        // =====================================================

        LocalDate startDate =
                yearMonth.atDay(1);

        LocalDate endDate =
                yearMonth.atEndOfMonth();

        // =====================================================
        // TOTAL ASSETS
        // =====================================================
        //
        // Assets are current-value based.
        //
        // Example:
        // Bank account       = 50,000
        // Gold               = 20,000
        // Other assets       = 10,000
        //
        // Total assets       = 80,000
        //
        // =====================================================

        BigDecimal totalAssets =
                assetRepository
                        .findByUserId(userId)
                        .stream()
                        .map(Asset::getCurrentValue)
                        .map(this::safeAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        // =====================================================
        // TOTAL LIABILITIES
        // =====================================================
        //
        // Uses outstanding amount.
        //
        // =====================================================

        BigDecimal totalLiabilities =
                liabilityRepository
                        .findByUserId(userId)
                        .stream()
                        .map(Liability::getOutstandingAmount)
                        .map(this::safeAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        // =====================================================
        // MONTHLY INCOME
        // =====================================================

        BigDecimal totalIncome =
                incomeRepository
                        .findByUserId(userId)
                        .stream()
                        .filter(income ->
                                income.getIncomeDate() != null
                                        && !income.getIncomeDate()
                                        .isBefore(startDate)
                                        && !income.getIncomeDate()
                                        .isAfter(endDate)
                        )
                        .map(Income::getAmount)
                        .map(this::safeAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        // =====================================================
        // MONTHLY EXPENSES
        // =====================================================

        BigDecimal totalExpenses =
                expenseRepository
                        .findByUserId(userId)
                        .stream()
                        .filter(expense ->
                                expense.getExpenseDate() != null
                                        && !expense.getExpenseDate()
                                        .isBefore(startDate)
                                        && !expense.getExpenseDate()
                                        .isAfter(endDate)
                        )
                        .map(Expense::getAmount)
                        .map(this::safeAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        // =====================================================
        // TOTAL INVESTMENTS
        // =====================================================
        //
        // Uses current investment value.
        //
        // =====================================================

        BigDecimal totalInvestments =
                investmentRepository
                        .findByUserId(userId)
                        .stream()
                        .map(Investment::getCurrentValue)
                        .map(this::safeAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        // =====================================================
        // NET WORTH
        // =====================================================
        //
        // Your project stores investments separately from assets.
        //
        // Therefore:
        //
        // Net Worth =
        // Assets + Investments - Liabilities
        //
        // =====================================================

        BigDecimal netWorth =
                totalAssets
                        .add(totalInvestments)
                        .subtract(totalLiabilities);

        // =====================================================
        // CREATE SNAPSHOT ENTITY
        // =====================================================

        MonthlySnapshot snapshot =
                new MonthlySnapshot();

        snapshot.setUserId(userId);
        snapshot.setSnapshotMonth(snapshotMonth);

        snapshot.setTotalAssets(totalAssets);
        snapshot.setTotalLiabilities(totalLiabilities);
        snapshot.setNetWorth(netWorth);
        snapshot.setTotalIncome(totalIncome);
        snapshot.setTotalExpenses(totalExpenses);
        snapshot.setTotalInvestments(totalInvestments);

        // =====================================================
        // SAVE
        // =====================================================

        MonthlySnapshot savedSnapshot =
                monthlySnapshotRepository.save(snapshot);

        // =====================================================
        // RESPONSE MAPPING
        // =====================================================

        return mapToResponse(savedSnapshot);
    }

    // =========================================================
    // GET ALL MY SNAPSHOTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<MonthlySnapshotResponse> getMySnapshots(
            Long userId) {

        return monthlySnapshotRepository
                .findByUserIdOrderBySnapshotMonthDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // GET SNAPSHOT BY MONTH
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public MonthlySnapshotResponse getSnapshotByMonth(
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

        LocalDate snapshotMonth =
                yearMonth.atDay(1);

        MonthlySnapshot snapshot =
                monthlySnapshotRepository
                        .findByUserIdAndSnapshotMonth(
                                userId,
                                snapshotMonth
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Monthly snapshot not found for "
                                                + month
                                )
                        );

        return mapToResponse(snapshot);
    }

    // =========================================================
    // RESPONSE MAPPING
    // =========================================================

    private MonthlySnapshotResponse mapToResponse(
            MonthlySnapshot snapshot) {

        return new MonthlySnapshotResponse(
                snapshot.getId(),
                snapshot.getUserId(),
                snapshot.getSnapshotMonth(),
                safeAmount(snapshot.getTotalAssets()),
                safeAmount(snapshot.getTotalLiabilities()),
                safeAmount(snapshot.getNetWorth()),
                safeAmount(snapshot.getTotalIncome()),
                safeAmount(snapshot.getTotalExpenses()),
                safeAmount(snapshot.getTotalInvestments()),
                snapshot.getCreatedAt()
        );
    }

    // =========================================================
    // SAFE BIGDECIMAL
    // =========================================================

    private BigDecimal safeAmount(BigDecimal amount) {

        return amount != null
                ? amount
                : BigDecimal.ZERO;
    }
}