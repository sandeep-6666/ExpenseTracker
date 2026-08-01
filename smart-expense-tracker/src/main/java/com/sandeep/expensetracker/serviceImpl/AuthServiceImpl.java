package com.sandeep.expensetracker.serviceImpl;

import com.sandeep.expensetracker.dto.JwtResponse;
import com.sandeep.expensetracker.dto.LoginRequest;
import com.sandeep.expensetracker.dto.RegisterRequest;
import com.sandeep.expensetracker.entity.Role;
import com.sandeep.expensetracker.entity.User;
import com.sandeep.expensetracker.exception.BadRequestException;
import com.sandeep.expensetracker.jwt.JwtUtil;
import com.sandeep.expensetracker.repository.UserRepository;
import com.sandeep.expensetracker.security.CustomUserDetails;
import com.sandeep.expensetracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implements registration and login, issuing JWTs on success. */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtResponse register(RegisterRequest request) {
        String email = request.getEmail() != null ? request.getEmail().toLowerCase().trim() : null;
        if (email != null && userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with this email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);
        log.info("New user registered: {}", saved.getEmail());

        CustomUserDetails userDetails = new CustomUserDetails(saved);
        String token = jwtUtil.generateToken(userDetails);

        return JwtResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(JwtResponse.UserDto.builder()
                        .userId(saved.getId())
                        .name(saved.getName())
                        .email(saved.getEmail())
                        .role(saved.getRole().name())
                        .build())
                .build();
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        String email = request.getEmail() != null ? request.getEmail().toLowerCase().trim() : null;
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtUtil.generateToken(userDetails);

        log.info("User logged in: {}", user.getEmail());

        return JwtResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(JwtResponse.UserDto.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .build())
                .build();
    }
}
