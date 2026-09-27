package com.walletmapx.backend.dto.investment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvestmentRequest {

    @NotBlank
    private String name;

    private Long assetId;

    @NotBlank
    private String investmentType;

    @Positive
    private BigDecimal quantity;

    @Positive
    private BigDecimal buyPrice;

    @Positive
    private BigDecimal investedAmount;

    private BigDecimal currentValue;

    private LocalDate investmentDate;

    private String notes;
}