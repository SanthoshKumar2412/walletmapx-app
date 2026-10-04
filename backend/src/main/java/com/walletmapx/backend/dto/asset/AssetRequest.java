package com.walletmapx.backend.dto.asset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AssetRequest {

	@NotNull(message = "Category is required")
	private Long categoryId;

    @NotBlank
    private String name;

    private String institution;

    @PositiveOrZero 
    private BigDecimal investedAmount;
    @PositiveOrZero 
    private BigDecimal currentValue;

    private LocalDate purchaseDate;

    private String notes;
}