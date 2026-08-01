package com.sandeep.expensetracker.service;

import com.sandeep.expensetracker.dto.IncomeRequest;
import com.sandeep.expensetracker.dto.IncomeResponse;

import java.util.List;

/** Business contract for income management. */
public interface IncomeService {
    IncomeResponse addIncome(String userEmail, IncomeRequest request);
    IncomeResponse updateIncome(String userEmail, Long incomeId, IncomeRequest request);
    void deleteIncome(String userEmail, Long incomeId);
    IncomeResponse getIncomeById(String userEmail, Long incomeId);
    List<IncomeResponse> getAllIncomes(String userEmail);
}
