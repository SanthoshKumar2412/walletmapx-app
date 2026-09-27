package com.walletmapx.backend.dto.asset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetResponse {

    private Long id;

    private Long userId;

    private Long categoryId;

    private String name;

    private String institution;

    private BigDecimal investedAmount;

    private BigDecimal currentValue;

    private LocalDate purchaseDate;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}