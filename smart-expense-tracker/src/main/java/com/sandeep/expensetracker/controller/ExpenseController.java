package com.sandeep.expensetracker.controller;

import com.sandeep.expensetracker.dto.ExpenseRequest;
import com.sandeep.expensetracker.dto.ExpenseResponse;
import com.sandeep.expensetracker.entity.Category;
import com.sandeep.expensetracker.response.ApiResponse;
import com.sandeep.expensetracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Endpoints for managing a user's expenses: CRUD, search and filtering. Delegates all logic to ExpenseService. */
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Expense management endpoints")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> addExpense(Authentication auth,
                                                                     @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.addExpense(auth.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Expense added", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(Authentication auth,
                                                                        @PathVariable Long id,
                                                                        @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.updateExpense(auth.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Expense updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(Authentication auth, @PathVariable Long id) {
        expenseService.deleteExpense(auth.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(Authentication auth, @PathVariable Long id) {
        ExpenseResponse response = expenseService.getExpenseById(auth.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Expense fetched", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAllExpenses(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Expenses fetched", expenseService.getAllExpenses(auth.getName())));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> search(Authentication auth,
                                                                       @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success("Search results", expenseService.searchExpenses(auth.getName(), keyword)));
    }

    @GetMapping("/filter/category")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> filterByCategory(Authentication auth,
                                                                                 @RequestParam Category category) {
        return ResponseEntity.ok(ApiResponse.success("Filtered by category", expenseService.filterByCategory(auth.getName(), category)));
    }

    @GetMapping("/filter/date")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> filterByDateRange(
            Authentication auth,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(ApiResponse.success("Filtered by date range", expenseService.filterByDateRange(auth.getName(), start, end)));
    }

    @GetMapping("/filter/month")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> filterByMonth(Authentication auth,
                                                                              @RequestParam int month,
                                                                              @RequestParam int year) {
        return ResponseEntity.ok(ApiResponse.success("Filtered by month", expenseService.filterByMonth(auth.getName(), month, year)));
    }

    @GetMapping("/filter/amount")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> filterByAmountRange(Authentication auth,
                                                                                    @RequestParam BigDecimal min,
                                                                                    @RequestParam BigDecimal max) {
        return ResponseEntity.ok(ApiResponse.success("Filtered by amount range", expenseService.filterByAmountRange(auth.getName(), min, max)));
    }
}
