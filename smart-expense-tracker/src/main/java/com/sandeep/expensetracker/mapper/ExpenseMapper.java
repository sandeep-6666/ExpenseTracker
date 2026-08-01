package com.sandeep.expensetracker.mapper;

import com.sandeep.expensetracker.dto.ExpenseRequest;
import com.sandeep.expensetracker.dto.ExpenseResponse;
import com.sandeep.expensetracker.entity.Expense;
import com.sandeep.expensetracker.entity.User;
import org.springframework.stereotype.Component;

/** Converts between Expense entity and its request/response DTOs. */
@Component
public class ExpenseMapper {

    public Expense toEntity(ExpenseRequest request, User user) {
        return Expense.builder()
                .user(user)
                .amount(request.getAmount())
                .category(request.getCategory())
                .description(request.getDescription())
                .expenseDate(request.getExpenseDate())
                .build();
    }

    public ExpenseResponse toResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .build();
    }
}
