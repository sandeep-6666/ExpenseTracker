package com.sandeep.expensetracker.mapper;

import com.sandeep.expensetracker.dto.IncomeRequest;
import com.sandeep.expensetracker.dto.IncomeResponse;
import com.sandeep.expensetracker.entity.Income;
import com.sandeep.expensetracker.entity.User;
import org.springframework.stereotype.Component;

/** Converts between Income entity and its request/response DTOs. */
@Component
public class IncomeMapper {

    public Income toEntity(IncomeRequest request, User user) {
        return Income.builder()
                .user(user)
                .amount(request.getAmount())
                .source(request.getSource())
                .incomeDate(request.getIncomeDate())
                .build();
    }

    public IncomeResponse toResponse(Income income) {
        return IncomeResponse.builder()
                .id(income.getId())
                .amount(income.getAmount())
                .source(income.getSource())
                .incomeDate(income.getIncomeDate())
                .build();
    }
}
