package com.walletmapx.backend.dto.liability;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiabilityResponse {

    private Long id;

    private Long userId;

    private String name;

    private String liabilityType;

    private BigDecimal principalAmount;

    private BigDecimal outstandingAmount;

    private BigDecimal interestRate;

    private BigDecimal monthlyEmi;

    private LocalDate startDate;

    private LocalDate endDate;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}