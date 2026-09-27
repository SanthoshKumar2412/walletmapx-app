package com.walletmapx.backend.dto.income;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class IncomeResponse {

    private Long id;
    private String category;
    private BigDecimal amount;
    private LocalDate incomeDate;
    private String description;
    private LocalDateTime createdAt;

    public IncomeResponse() {
    }

    public IncomeResponse(
            Long id,
            String category,
            BigDecimal amount,
            LocalDate incomeDate,
            String description,
            LocalDateTime createdAt) {

        this.id = id;
        this.category = category;
        this.amount = amount;
        this.incomeDate = incomeDate;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getIncomeDate() {
        return incomeDate;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}