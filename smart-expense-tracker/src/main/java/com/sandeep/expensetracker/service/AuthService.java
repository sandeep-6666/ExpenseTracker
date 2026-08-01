package com.sandeep.expensetracker.service;

import com.sandeep.expensetracker.dto.JwtResponse;
import com.sandeep.expensetracker.dto.LoginRequest;
import com.sandeep.expensetracker.dto.RegisterRequest;

/** Business contract for authentication operations. */
public interface AuthService {
    JwtResponse register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
}
