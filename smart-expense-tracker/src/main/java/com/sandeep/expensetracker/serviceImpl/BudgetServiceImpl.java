package com.sandeep.expensetracker.serviceImpl;

import com.sandeep.expensetracker.dto.BudgetRequest;
import com.sandeep.expensetracker.dto.BudgetResponse;
import com.sandeep.expensetracker.entity.Budget;
import com.sandeep.expensetracker.entity.User;
import com.sandeep.expensetracker.exception.ResourceNotFoundException;
import com.sandeep.expensetracker.repository.BudgetRepository;
import com.sandeep.expensetracker.repository.ExpenseRepository;
import com.sandeep.expensetracker.repository.UserRepository;
import com.sandeep.expensetracker.service.BudgetService;
import com.sandeep.expensetracker.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Implements monthly budget setting and progress/warning calculation. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetServiceImpl implements BudgetService {

    private static final Logger log = LoggerFactory.getLogger(BudgetServiceImpl.class);

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    private User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    @Transactional
    public BudgetResponse setBudget(String userEmail, BudgetRequest request) {
        User user = getUserOrThrow(userEmail);

        Budget budget = budgetRepository.findByUserAndMonthAndYear(user, request.getMonth(), request.getYear())
                .orElse(Budget.builder().user(user).month(request.getMonth()).year(request.getYear()).build());

        budget.setMonthlyLimit(request.getMonthlyLimit());
        Budget saved = budgetRepository.save(budget);
        log.info("Budget set for user {} ({}/{}): limit={}", userEmail, request.getMonth(), request.getYear(), request.getMonthlyLimit());

        return buildResponse(saved, user);
    }

    @Override
    public BudgetResponse getBudgetStatus(String userEmail, int month, int year) {
        User user = getUserOrThrow(userEmail);
        Budget budget = budgetRepository.findByUserAndMonthAndYear(user, month, year)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No budget set for " + month + "/" + year));
        return buildResponse(budget, user);
    }

    private BudgetResponse buildResponse(Budget budget, User user) {
        BigDecimal spent = expenseRepository.sumByUserAndMonth(user, budget.getMonth(), budget.getYear());
        BigDecimal remaining = budget.getMonthlyLimit().subtract(spent);

        double percentUsed = budget.getMonthlyLimit().compareTo(BigDecimal.ZERO) == 0
                ? 0.0
                : spent.divide(budget.getMonthlyLimit(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();

        boolean warning = budget.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0 &&
                spent.compareTo(budget.getMonthlyLimit().multiply(AppConstants.BUDGET_WARNING_THRESHOLD)) >= 0;

        return BudgetResponse.builder()
                .id(budget.getId())
                .month(budget.getMonth())
                .year(budget.getYear())
                .monthlyLimit(budget.getMonthlyLimit())
                .totalSpent(spent)
                .remaining(remaining)
                .percentUsed(percentUsed)
                .warning(warning)
                .build();
    }
}
