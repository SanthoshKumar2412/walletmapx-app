package com.walletmapx.backend.dto.liability;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LiabilityRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String liabilityType;

    @Positive
    private BigDecimal principalAmount;

    @Positive
    private BigDecimal outstandingAmount;

    @Positive
    private BigDecimal interestRate;

    @Positive
    private BigDecimal monthlyEmi;

    private LocalDate startDate;

    private LocalDate endDate;

    private String notes;
}