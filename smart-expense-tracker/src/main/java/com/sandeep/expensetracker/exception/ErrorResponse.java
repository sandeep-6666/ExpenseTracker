package com.sandeep.expensetracker.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/** Standard error body returned by the GlobalExceptionHandler. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private boolean success;
    private int status;
    private String message;
    private Map<String, String> fieldErrors;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
