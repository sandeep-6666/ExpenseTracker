package com.sandeep.expensetracker.dto;

import com.sandeep.expensetracker.entity.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Payload for creating/updating an expense. */
@Data
public class ExpenseRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Category is required")
    private Category category;

    private String description;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;
}
