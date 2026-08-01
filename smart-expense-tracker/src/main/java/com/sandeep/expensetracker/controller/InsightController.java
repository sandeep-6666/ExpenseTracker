package com.sandeep.expensetracker.controller;

import com.sandeep.expensetracker.dto.InsightResponse;
import com.sandeep.expensetracker.response.ApiResponse;
import com.sandeep.expensetracker.service.AiInsightService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Endpoint exposing AI-generated (rule-based) spending insights. Delegates to AiInsightService. */
@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
@Tag(name = "AI Insights", description = "Rule-based spending insights, pluggable for future GenAI backing")
public class InsightController {

    private final AiInsightService aiInsightService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InsightResponse>>> getInsights(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Insights generated", aiInsightService.generateInsights(auth.getName())));
    }
}
