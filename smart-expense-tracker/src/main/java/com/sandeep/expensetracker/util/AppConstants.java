package com.sandeep.expensetracker.util;

import java.math.BigDecimal;

/** Application-wide constants. */
public final class AppConstants {

    private AppConstants() {
    }

    public static final BigDecimal BUDGET_WARNING_THRESHOLD = new BigDecimal("0.80"); // 80%
}
