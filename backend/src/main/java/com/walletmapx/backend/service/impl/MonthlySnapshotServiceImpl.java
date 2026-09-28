package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.monthlysnapshot.MonthlySnapshotRequest;
import com.walletmapx.backend.dto.monthlysnapshot.MonthlySnapshotResponse;
import com.walletmapx.backend.entity.MonthlySnapshot;
import com.walletmapx.backend.exception.BadRequestException;
import com.walletmapx.backend.exception.ResourceNotFoundException;
import com.walletmapx.backend.repository.MonthlySnapshotRepository;
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
@Transactional
public class MonthlySnapshotServiceImpl
        implements MonthlySnapshotService {

    private final MonthlySnapshotRepository monthlySnapshotRepository;
    private final FinancialAggregationService financialAggregationService;

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

        // Always store the first day of the month so
        // findByUserIdAndSnapshotMonth(...) stays consistent.
        YearMonth yearMonth =
                YearMonth.from(request.getSnapshotMonth());

        LocalDate snapshotMonth =
                yearMonth.atDay(1);

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

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        BigDecimal totalAssets =
                financialAggregationService.totalAssets(userId);

        BigDecimal totalLiabilities =
                financialAggregationService.totalLiabilities(userId);

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

        BigDecimal totalInvestments =
                financialAggregationService.totalInvestments(userId);

        // Net Worth = Assets + Investments - Liabilities
        BigDecimal netWorth = totalAssets
                .add(totalInvestments)
                .subtract(totalLiabilities);

        MonthlySnapshot snapshot = new MonthlySnapshot();

        snapshot.setUserId(userId);
        snapshot.setSnapshotMonth(snapshotMonth);
        snapshot.setTotalAssets(totalAssets);
        snapshot.setTotalLiabilities(totalLiabilities);
        snapshot.setNetWorth(netWorth);
        snapshot.setTotalIncome(totalIncome);
        snapshot.setTotalExpenses(totalExpenses);
        snapshot.setTotalInvestments(totalInvestments);

        MonthlySnapshot savedSnapshot =
                monthlySnapshotRepository.save(snapshot);

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

    private BigDecimal safeAmount(BigDecimal amount) {

        return amount != null
                ? amount
                : BigDecimal.ZERO;
    }
}
