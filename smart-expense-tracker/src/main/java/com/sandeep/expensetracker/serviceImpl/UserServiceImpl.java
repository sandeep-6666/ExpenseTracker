package com.sandeep.expensetracker.serviceImpl;

import com.sandeep.expensetracker.dto.ChangePasswordRequest;
import com.sandeep.expensetracker.dto.UpdateProfileRequest;
import com.sandeep.expensetracker.dto.UserProfileResponse;
import com.sandeep.expensetracker.entity.User;
import com.sandeep.expensetracker.exception.BadRequestException;
import com.sandeep.expensetracker.exception.ResourceNotFoundException;
import com.sandeep.expensetracker.repository.UserRepository;
import com.sandeep.expensetracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private UserProfileResponse toResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public UserProfileResponse getProfile(String email) {
        return toResponse(getUserOrThrow(email));
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = getUserOrThrow(email);
        user.setName(request.getName());
        User saved = userRepository.save(user);
        log.info("Profile updated for user {}", email);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = getUserOrThrow(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user {}", email);
    }
}
