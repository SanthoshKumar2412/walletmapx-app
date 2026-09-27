package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.investment.InvestmentRequest;
import com.walletmapx.backend.dto.investment.InvestmentResponse;

import java.util.List;

public interface InvestmentService {

    InvestmentResponse createInvestment(
            Long userId,
            InvestmentRequest request
    );

    List<InvestmentResponse> getAllInvestments(
            Long userId
    );

    InvestmentResponse getInvestmentById(
            Long userId,
            Long investmentId
    );

    InvestmentResponse updateInvestment(
            Long userId,
            Long investmentId,
            InvestmentRequest request
    );

    void deleteInvestment(
            Long userId,
            Long investmentId
    );
}