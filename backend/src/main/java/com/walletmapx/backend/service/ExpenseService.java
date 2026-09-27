package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.expense.ExpenseRequest;
import com.walletmapx.backend.dto.expense.ExpenseResponse;

import java.util.List;

public interface ExpenseService {

    ExpenseResponse createExpense(
            Long userId,
            ExpenseRequest request
    );

    List<ExpenseResponse> getAllExpenses(
            Long userId
    );

    ExpenseResponse getExpenseById(
            Long userId,
            Long expenseId
    );

    ExpenseResponse updateExpense(
            Long userId,
            Long expenseId,
            ExpenseRequest request
    );

    void deleteExpense(
            Long userId,
            Long expenseId
    );
}