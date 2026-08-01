package com.sandeep.expensetracker.service;

import com.sandeep.expensetracker.dto.DashboardResponse;

/** Business contract for aggregated dashboard data. */
public interface DashboardService {
    DashboardResponse getDashboard(String userEmail);
}
