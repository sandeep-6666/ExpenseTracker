package com.sandeep.expensetracker.serviceImpl;

import com.sandeep.expensetracker.dto.ExpenseRequest;
import com.sandeep.expensetracker.dto.ExpenseResponse;
import com.sandeep.expensetracker.entity.Category;
import com.sandeep.expensetracker.entity.Expense;
import com.sandeep.expensetracker.entity.User;
import com.sandeep.expensetracker.exception.ResourceNotFoundException;
import com.sandeep.expensetracker.mapper.ExpenseMapper;
import com.sandeep.expensetracker.repository.ExpenseRepository;
import com.sandeep.expensetracker.repository.UserRepository;
import com.sandeep.expensetracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Implements CRUD, search and filtering business logic for expenses. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseServiceImpl implements ExpenseService {

    private static final Logger log = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final ExpenseMapper expenseMapper;

    private User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private Expense getOwnedExpenseOrThrow(User user, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Expense not found with id: " + expenseId);
        }
        return expense;
    }

    @Override
    @Transactional
    public ExpenseResponse addExpense(String userEmail, ExpenseRequest request) {
        User user = getUserOrThrow(userEmail);
        Expense saved = expenseRepository.save(expenseMapper.toEntity(request, user));
        log.info("Expense added for user {}: amount={}, category={}", userEmail, saved.getAmount(), saved.getCategory());
        return expenseMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ExpenseResponse updateExpense(String userEmail, Long expenseId, ExpenseRequest request) {
        User user = getUserOrThrow(userEmail);
        Expense expense = getOwnedExpenseOrThrow(user, expenseId);

        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());

        Expense updated = expenseRepository.save(expense);
        log.info("Expense {} updated for user {}", expenseId, userEmail);
        return expenseMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteExpense(String userEmail, Long expenseId) {
        User user = getUserOrThrow(userEmail);
        Expense expense = getOwnedExpenseOrThrow(user, expenseId);
        expenseRepository.delete(expense);
        log.info("Expense {} deleted for user {}", expenseId, userEmail);
    }

    @Override
    public ExpenseResponse getExpenseById(String userEmail, Long expenseId) {
        User user = getUserOrThrow(userEmail);
        return expenseMapper.toResponse(getOwnedExpenseOrThrow(user, expenseId));
    }

    @Override
    public List<ExpenseResponse> getAllExpenses(String userEmail) {
        User user = getUserOrThrow(userEmail);
        return expenseRepository.findByUserOrderByExpenseDateDesc(user)
                .stream().map(expenseMapper::toResponse).toList();
    }

    @Override
    public List<ExpenseResponse> searchExpenses(String userEmail, String keyword) {
        User user = getUserOrThrow(userEmail);
        return expenseRepository.searchByDescription(user, keyword)
                .stream().map(expenseMapper::toResponse).toList();
    }

    @Override
    public List<ExpenseResponse> filterByCategory(String userEmail, Category category) {
        User user = getUserOrThrow(userEmail);
        return expenseRepository.findByUserAndCategory(user, category)
                .stream().map(expenseMapper::toResponse).toList();
    }

    @Override
    public List<ExpenseResponse> filterByDateRange(String userEmail, LocalDate start, LocalDate end) {
        User user = getUserOrThrow(userEmail);
        return expenseRepository.findByUserAndExpenseDateBetween(user, start, end)
                .stream().map(expenseMapper::toResponse).toList();
    }

    @Override
    public List<ExpenseResponse> filterByMonth(String userEmail, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return filterByDateRange(userEmail, start, end);
    }

    @Override
    public List<ExpenseResponse> filterByAmountRange(String userEmail, BigDecimal min, BigDecimal max) {
        User user = getUserOrThrow(userEmail);
        return expenseRepository.findByUserAndAmountBetween(user, min, max)
                .stream().map(expenseMapper::toResponse).toList();
    }
}
