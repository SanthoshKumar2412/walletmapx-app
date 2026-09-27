package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.expense.ExpenseRequest;
import com.walletmapx.backend.dto.expense.ExpenseResponse;
import com.walletmapx.backend.entity.Expense;
import com.walletmapx.backend.exception.ResourceNotFoundException;
import com.walletmapx.backend.repository.ExpenseRepository;
import com.walletmapx.backend.service.ExpenseService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Override
    public ExpenseResponse createExpense(
            Long userId,
            ExpenseRequest request) {

        Expense expense = Expense.builder()
                .userId(userId)
                .category(request.getCategory())
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .description(request.getDescription())
                .build();

        Expense savedExpense = expenseRepository.save(expense);

        return mapToResponse(savedExpense);
    }

    @Override
    public List<ExpenseResponse> getAllExpenses(Long userId) {

        return expenseRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ExpenseResponse getExpenseById(
            Long userId,
            Long expenseId) {

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense not found"
                        ));

        checkOwnership(expense, userId);

        return mapToResponse(expense);
    }

    @Override
    public ExpenseResponse updateExpense(
            Long userId,
            Long expenseId,
            ExpenseRequest request) {

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense not found"
                        ));

        checkOwnership(expense, userId);

        expense.setCategory(request.getCategory());
        expense.setAmount(request.getAmount());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setDescription(request.getDescription());

        Expense updatedExpense =
                expenseRepository.save(expense);

        return mapToResponse(updatedExpense);
    }

    @Override
    public void deleteExpense(
            Long userId,
            Long expenseId) {

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense not found"
                        ));

        checkOwnership(expense, userId);

        expenseRepository.delete(expense);
    }

    private void checkOwnership(
            Expense expense,
            Long userId) {

        if (!expense.getUserId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Expense not found"
            );
        }
    }

    private ExpenseResponse mapToResponse(
            Expense expense) {

        return new ExpenseResponse(
                expense.getId(),
                expense.getUserId(),
                expense.getCategory(),
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getDescription(),
                expense.getCreatedAt()
        );
    }
}