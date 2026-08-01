package com.sandeep.expensetracker.controller;

import com.sandeep.expensetracker.dto.BudgetRequest;
import com.sandeep.expensetracker.dto.BudgetResponse;
import com.sandeep.expensetracker.response.ApiResponse;
import com.sandeep.expensetracker.service.BudgetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** Endpoints for setting and checking monthly budgets. Delegates all logic to BudgetService. */
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "Budget", description = "Monthly budget management endpoints")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> setBudget(Authentication auth,
                                                                   @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Budget saved", budgetService.setBudget(auth.getName(), request)));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudgetStatus(Authentication auth,
                                                                         @RequestParam int month,
                                                                         @RequestParam int year) {
        return ResponseEntity.ok(ApiResponse.success("Budget status fetched", budgetService.getBudgetStatus(auth.getName(), month, year)));
    }
}
