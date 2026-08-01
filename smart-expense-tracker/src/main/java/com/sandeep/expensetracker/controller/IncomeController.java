package com.sandeep.expensetracker.controller;

import com.sandeep.expensetracker.dto.IncomeRequest;
import com.sandeep.expensetracker.dto.IncomeResponse;
import com.sandeep.expensetracker.response.ApiResponse;
import com.sandeep.expensetracker.service.IncomeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoints for managing a user's income entries. Delegates all logic to IncomeService. */
@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
@Tag(name = "Income", description = "Income management endpoints")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<ApiResponse<IncomeResponse>> addIncome(Authentication auth,
                                                                   @Valid @RequestBody IncomeRequest request) {
        IncomeResponse response = incomeService.addIncome(auth.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Income added", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IncomeResponse>> updateIncome(Authentication auth,
                                                                      @PathVariable Long id,
                                                                      @Valid @RequestBody IncomeRequest request) {
        IncomeResponse response = incomeService.updateIncome(auth.getName(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Income updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIncome(Authentication auth, @PathVariable Long id) {
        incomeService.deleteIncome(auth.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Income deleted", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IncomeResponse>> getIncomeById(Authentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Income fetched", incomeService.getIncomeById(auth.getName(), id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<IncomeResponse>>> getAllIncomes(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Incomes fetched", incomeService.getAllIncomes(auth.getName())));
    }
}
