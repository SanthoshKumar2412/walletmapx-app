package com.walletmapx.backend.dto.liability;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LiabilityRequest {

	@NotBlank
	private String name;

	@NotBlank
	private String liabilityType;

	@NotNull @Positive
	private BigDecimal principalAmount;

	@NotNull @PositiveOrZero
	private BigDecimal outstandingAmount;

	@NotNull @PositiveOrZero
	private BigDecimal interestRate;

	@NotNull @PositiveOrZero
	private BigDecimal monthlyEmi;

	private LocalDate startDate;
	private LocalDate endDate;
	private String notes;
}