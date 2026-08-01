package com.sandeep.expensetracker.serviceImpl;

import com.sandeep.expensetracker.dto.InsightResponse;
import com.sandeep.expensetracker.entity.Category;
import com.sandeep.expensetracker.entity.User;
import com.sandeep.expensetracker.exception.ResourceNotFoundException;
import com.sandeep.expensetracker.repository.ExpenseRepository;
import com.sandeep.expensetracker.repository.UserRepository;
import com.sandeep.expensetracker.service.AiInsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rule-based implementation of AiInsightService.
 *
 * This class deliberately contains ONLY the analysis logic and depends only on
 * the AiInsightService interface contract (month-over-month category sums,
 * medical/entertainment thresholds, projected savings). Because callers
 * (the controller) depend on the AiInsightService interface rather than this
 * class directly, a future GenAiInsightServiceImpl backed by Gemini/OpenAI can
 * be introduced later and swapped in via Spring's @Primary/@Qualifier or a
 * feature flag, with zero changes to the controller layer.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RuleBasedAiInsightServiceImpl implements AiInsightService {

    private static final BigDecimal MEDICAL_HIGH_THRESHOLD = new BigDecimal("5000");
    private static final BigDecimal SIGNIFICANT_INCREASE_PERCENT = new BigDecimal("20"); // 20%

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    private User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    public List<InsightResponse> generateInsights(String userEmail) {
        User user = getUserOrThrow(userEmail);
        List<InsightResponse> insights = new ArrayList<>();

        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);

        Map<String, BigDecimal> currentMonth = toMap(
                expenseRepository.sumByCategoryForUserAndMonth(user, now.getMonthValue(), now.getYear()));
        Map<String, BigDecimal> previousMonth = toMap(
                expenseRepository.sumByCategoryForUserAndMonth(user, lastMonth.getMonthValue(), lastMonth.getYear()));

        // Rule 1: category spending increased significantly month-over-month
        for (Map.Entry<String, BigDecimal> entry : currentMonth.entrySet()) {
            String category = entry.getKey();
            BigDecimal current = entry.getValue();
            BigDecimal previous = previousMonth.getOrDefault(category, BigDecimal.ZERO);

            if (previous.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentChange = current.subtract(previous)
                        .divide(previous, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

                if (percentChange.compareTo(SIGNIFICANT_INCREASE_PERCENT) >= 0) {
                    insights.add(InsightResponse.builder()
                            .type("WARNING")
                            .message(String.format("You spent %.0f%% more on %s this month compared to last month.",
                                    percentChange, formatCategory(category)))
                            .build());
                }
            }
        }

        // Rule 2: unusually high medical expenses
        BigDecimal medicalSpend = currentMonth.getOrDefault(Category.MEDICAL.name(), BigDecimal.ZERO);
        if (medicalSpend.compareTo(MEDICAL_HIGH_THRESHOLD) > 0) {
            insights.add(InsightResponse.builder()
                    .type("WARNING")
                    .message("Medical expenses are unusually high this month.")
                    .build());
        }

        // Rule 3: entertainment spending trend
        BigDecimal entertainmentCurrent = currentMonth.getOrDefault(Category.ENTERTAINMENT.name(), BigDecimal.ZERO);
        BigDecimal entertainmentPrevious = previousMonth.getOrDefault(Category.ENTERTAINMENT.name(), BigDecimal.ZERO);
        if (entertainmentCurrent.compareTo(entertainmentPrevious) > 0 && entertainmentPrevious.compareTo(BigDecimal.ZERO) > 0) {
            insights.add(InsightResponse.builder()
                    .type("INFO")
                    .message("Your entertainment spending has increased compared to last month.")
                    .build());
        }

        // Rule 4: projected savings tip if FOOD spend is high relative to income-independent baseline
        BigDecimal foodSpend = currentMonth.getOrDefault(Category.FOOD.name(), BigDecimal.ZERO);
        if (foodSpend.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal potentialSaving = foodSpend.multiply(new BigDecimal("0.15")).setScale(0, RoundingMode.HALF_UP);
            if (potentialSaving.compareTo(BigDecimal.ZERO) > 0) {
                insights.add(InsightResponse.builder()
                        .type("TIP")
                        .message(String.format("You could save around ₹%s next month by reducing restaurant/food spending.",
                                potentialSaving.toPlainString()))
                        .build());
            }
        }

        if (insights.isEmpty()) {
            insights.add(InsightResponse.builder()
                    .type("INFO")
                    .message("No unusual spending patterns detected this month. Keep it up!")
                    .build());
        }

        return insights;
    }

    private Map<String, BigDecimal> toMap(List<Object[]> rows) {
        Map<String, BigDecimal> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put(row[0].toString(), (BigDecimal) row[1]);
        }
        return map;
    }

    private String formatCategory(String category) {
        return category.charAt(0) + category.substring(1).toLowerCase();
    }
}
