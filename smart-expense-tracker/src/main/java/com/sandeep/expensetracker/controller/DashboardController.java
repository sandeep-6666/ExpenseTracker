package com.sandeep.expensetracker.controller;

import com.sandeep.expensetracker.dto.DashboardResponse;
import com.sandeep.expensetracker.response.ApiResponse;
import com.sandeep.expensetracker.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint providing aggregated dashboard data (totals, balance, charts). Delegates to DashboardService. */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Aggregated dashboard data for charts and summaries")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Dashboard data fetched", dashboardService.getDashboard(auth.getName())));
    }
}
