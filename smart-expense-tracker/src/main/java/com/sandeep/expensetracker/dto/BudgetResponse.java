package com.sandeep.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Response shape for budget status, including progress and warning flag. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {
    private Long id;
    private int month;
    private int year;
    private BigDecimal monthlyLimit;
    private BigDecimal totalSpent;
    private BigDecimal remaining;
    private double percentUsed;
    private boolean warning; // true once 80% threshold crossed
}
