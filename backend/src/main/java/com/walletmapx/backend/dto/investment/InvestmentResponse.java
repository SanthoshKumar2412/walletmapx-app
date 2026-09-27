package com.walletmapx.backend.dto.investment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentResponse {

    private Long id;

    private Long userId;

    private String name;

    private String investmentType;

    private BigDecimal quantity;

    private BigDecimal buyPrice;

    private BigDecimal investedAmount;

    private BigDecimal currentValue;

    private LocalDate investmentDate;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}