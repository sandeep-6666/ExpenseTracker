package com.sandeep.expensetracker.serviceImpl;

import com.sandeep.expensetracker.dto.DashboardResponse;
import com.sandeep.expensetracker.entity.User;
import com.sandeep.expensetracker.exception.ResourceNotFoundException;
import com.sandeep.expensetracker.repository.ExpenseRepository;
import com.sandeep.expensetracker.repository.IncomeRepository;
import com.sandeep.expensetracker.repository.UserRepository;
import com.sandeep.expensetracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/** Aggregates income, expense and category data for the dashboard view. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    private User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    public DashboardResponse getDashboard(String userEmail) {
        User user = getUserOrThrow(userEmail);

        BigDecimal totalIncome = incomeRepository.sumByUser(user);
        BigDecimal totalExpense = expenseRepository.sumByUser(user);
        BigDecimal balance = totalIncome.subtract(totalExpense);

        Map<String, BigDecimal> categoryWise = new LinkedHashMap<>();
        for (Object[] row : expenseRepository.sumByCategoryForUser(user)) {
            categoryWise.put(row[0].toString(), (BigDecimal) row[1]);
        }

        List<DashboardResponse.MonthlyTrend> trend = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            int month = monthDate.getMonthValue();
            int year = monthDate.getYear();
            BigDecimal income = incomeRepository.sumByUserAndMonth(user, month, year);
            BigDecimal expense = expenseRepository.sumByUserAndMonth(user, month, year);
            trend.add(DashboardResponse.MonthlyTrend.builder()
                    .month(monthDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year)
                    .income(income)
                    .expense(expense)
                    .build());
        }

        return DashboardResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .currentBalance(balance)
                .categoryWiseSpending(categoryWise)
                .monthlyTrend(trend)
                .build();
    }
}
