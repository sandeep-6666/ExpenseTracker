package com.sandeep.expensetracker.service;

import com.sandeep.expensetracker.dto.InsightResponse;

import java.util.List;

/**
 * Business contract for AI-driven spending insights.
 * Current implementation is rule-based; designed so a future implementation
 * (e.g. backed by Gemini/OpenAI) can be swapped in without changing callers.
 */
public interface AiInsightService {
    List<InsightResponse> generateInsights(String userEmail);
}
