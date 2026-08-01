package com.sandeep.expensetracker.serviceImpl;

import com.sandeep.expensetracker.dto.IncomeRequest;
import com.sandeep.expensetracker.dto.IncomeResponse;
import com.sandeep.expensetracker.entity.Income;
import com.sandeep.expensetracker.entity.User;
import com.sandeep.expensetracker.exception.ResourceNotFoundException;
import com.sandeep.expensetracker.mapper.IncomeMapper;
import com.sandeep.expensetracker.repository.IncomeRepository;
import com.sandeep.expensetracker.repository.UserRepository;
import com.sandeep.expensetracker.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Implements CRUD business logic for income entries. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncomeServiceImpl implements IncomeService {

    private static final Logger log = LoggerFactory.getLogger(IncomeServiceImpl.class);

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;
    private final IncomeMapper incomeMapper;

    private User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private Income getOwnedIncomeOrThrow(User user, Long incomeId) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + incomeId));
        if (!income.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Income not found with id: " + incomeId);
        }
        return income;
    }

    @Override
    @Transactional
    public IncomeResponse addIncome(String userEmail, IncomeRequest request) {
        User user = getUserOrThrow(userEmail);
        Income saved = incomeRepository.save(incomeMapper.toEntity(request, user));
        log.info("Income added for user {}: amount={}, source={}", userEmail, saved.getAmount(), saved.getSource());
        return incomeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public IncomeResponse updateIncome(String userEmail, Long incomeId, IncomeRequest request) {
        User user = getUserOrThrow(userEmail);
        Income income = getOwnedIncomeOrThrow(user, incomeId);

        income.setAmount(request.getAmount());
        income.setSource(request.getSource());
        income.setIncomeDate(request.getIncomeDate());

        Income updated = incomeRepository.save(income);
        log.info("Income {} updated for user {}", incomeId, userEmail);
        return incomeMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteIncome(String userEmail, Long incomeId) {
        User user = getUserOrThrow(userEmail);
        Income income = getOwnedIncomeOrThrow(user, incomeId);
        incomeRepository.delete(income);
        log.info("Income {} deleted for user {}", incomeId, userEmail);
    }

    @Override
    public IncomeResponse getIncomeById(String userEmail, Long incomeId) {
        User user = getUserOrThrow(userEmail);
        return incomeMapper.toResponse(getOwnedIncomeOrThrow(user, incomeId));
    }

    @Override
    public List<IncomeResponse> getAllIncomes(String userEmail) {
        User user = getUserOrThrow(userEmail);
        return incomeRepository.findByUserOrderByIncomeDateDesc(user)
                .stream().map(incomeMapper::toResponse).toList();
    }
}
