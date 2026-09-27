package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.income.IncomeRequest;
import com.walletmapx.backend.dto.income.IncomeResponse;

import java.util.List;

public interface IncomeService {

    IncomeResponse createIncome(Long userId, IncomeRequest request);

    List<IncomeResponse> getAllIncome(Long userId);

    IncomeResponse getIncomeById(Long userId, Long incomeId);

    IncomeResponse updateIncome(
            Long userId,
            Long incomeId,
            IncomeRequest request
    );

    void deleteIncome(Long userId, Long incomeId);
}