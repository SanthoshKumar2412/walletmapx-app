package com.walletmapx.backend.dto.asset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AssetRequest {

    private Long categoryId;

    @NotBlank
    private String name;

    private String institution;

    @Positive
    private BigDecimal investedAmount;

    @Positive
    private BigDecimal currentValue;

    private LocalDate purchaseDate;

    private String notes;
}