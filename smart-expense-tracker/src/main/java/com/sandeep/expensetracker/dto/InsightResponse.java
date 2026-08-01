package com.sandeep.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A single AI-generated (rule-based) insight message. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsightResponse {
    private String type;     // e.g. "WARNING", "INFO", "TIP"
    private String message;
}
