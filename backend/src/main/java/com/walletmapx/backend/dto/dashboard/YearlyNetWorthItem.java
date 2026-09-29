package com.walletmapx.backend.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearlyNetWorthItem {

    private LocalDate month;

    private BigDecimal netWorth;
}