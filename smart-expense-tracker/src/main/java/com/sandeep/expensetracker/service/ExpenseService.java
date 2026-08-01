package com.sandeep.expensetracker.service;

import com.sandeep.expensetracker.dto.ExpenseRequest;
import com.sandeep.expensetracker.dto.ExpenseResponse;
import com.sandeep.expensetracker.entity.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Business contract for expense management. */
public interface ExpenseService {
    ExpenseResponse addExpense(String userEmail, ExpenseRequest request);
    ExpenseResponse updateExpense(String userEmail, Long expenseId, ExpenseRequest request);
    void deleteExpense(String userEmail, Long expenseId);
    ExpenseResponse getExpenseById(String userEmail, Long expenseId);
    List<ExpenseResponse> getAllExpenses(String userEmail);
    List<ExpenseResponse> searchExpenses(String userEmail, String keyword);
    List<ExpenseResponse> filterByCategory(String userEmail, Category category);
    List<ExpenseResponse> filterByDateRange(String userEmail, LocalDate start, LocalDate end);
    List<ExpenseResponse> filterByMonth(String userEmail, int month, int year);
    List<ExpenseResponse> filterByAmountRange(String userEmail, BigDecimal min, BigDecimal max);
}
