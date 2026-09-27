package com.walletmapx.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_snapshots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "snapshot_month", nullable = false)
    private LocalDate snapshotMonth;

    @Column(
            name = "total_assets",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal totalAssets = BigDecimal.ZERO;

    @Column(
            name = "total_liabilities",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal totalLiabilities = BigDecimal.ZERO;

    @Column(
            name = "net_worth",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal netWorth = BigDecimal.ZERO;

    @Column(
            name = "total_income",
            precision = 15,
            scale = 2
    )
    private BigDecimal totalIncome = BigDecimal.ZERO;

    @Column(
            name = "total_expenses",
            precision = 15,
            scale = 2
    )
    private BigDecimal totalExpenses = BigDecimal.ZERO;

    @Column(
            name = "total_investments",
            precision = 15,
            scale = 2
    )
    private BigDecimal totalInvestments = BigDecimal.ZERO;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}