package com.sandeep.expensetracker.service;

import com.sandeep.expensetracker.dto.BudgetRequest;
import com.sandeep.expensetracker.dto.BudgetResponse;

/** Business contract for monthly budget management. */
public interface BudgetService {
    BudgetResponse setBudget(String userEmail, BudgetRequest request);
    BudgetResponse getBudgetStatus(String userEmail, int month, int year);
}
