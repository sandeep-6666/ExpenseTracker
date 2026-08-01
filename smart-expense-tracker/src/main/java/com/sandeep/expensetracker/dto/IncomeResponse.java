package com.sandeep.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Response shape for an income entry returned to the client. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeResponse {
    private Long id;
    private BigDecimal amount;
    private String source;
    private LocalDate incomeDate;
}
