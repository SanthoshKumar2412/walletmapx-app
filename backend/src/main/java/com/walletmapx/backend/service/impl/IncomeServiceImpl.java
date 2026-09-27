package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.income.IncomeRequest;
import com.walletmapx.backend.dto.income.IncomeResponse;
import com.walletmapx.backend.entity.Income;
import com.walletmapx.backend.exception.ResourceNotFoundException;
import com.walletmapx.backend.repository.IncomeRepository;
import com.walletmapx.backend.service.IncomeService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;

    @Override
    public IncomeResponse createIncome(Long userId, IncomeRequest request) {

        Income income = Income.builder()
                .userId(userId)
                .category(request.getCategory())
                .amount(request.getAmount())
                .incomeDate(request.getIncomeDate())
                .description(request.getDescription())
                .build();

        Income savedIncome = incomeRepository.save(income);

        return mapToResponse(savedIncome);
    }

    @Override
    public List<IncomeResponse> getAllIncome(Long userId) {

        return incomeRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public IncomeResponse getIncomeById(Long userId, Long incomeId) {

        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Income not found"));

        checkOwnership(income, userId);

        return mapToResponse(income);
    }

    @Override
    public IncomeResponse updateIncome(
            Long userId,
            Long incomeId,
            IncomeRequest request) {

        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Income not found"));

        checkOwnership(income, userId);

        income.setCategory(request.getCategory());
        income.setAmount(request.getAmount());
        income.setIncomeDate(request.getIncomeDate());
        income.setDescription(request.getDescription());

        Income updatedIncome = incomeRepository.save(income);

        return mapToResponse(updatedIncome);
    }

    @Override
    public void deleteIncome(Long userId, Long incomeId) {

        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Income not found"));

        checkOwnership(income, userId);

        incomeRepository.delete(income);
    }

    private void checkOwnership(Income income, Long userId) {

        if (!income.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Income not found");
        }
    }

    private IncomeResponse mapToResponse(Income income) {

        return new IncomeResponse(
                income.getId(),
                income.getCategory(),
                income.getAmount(),
                income.getIncomeDate(),
                income.getDescription(),
                income.getCreatedAt()
        );
    }
}