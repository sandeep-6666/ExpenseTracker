package com.sandeep.expensetracker.service;

import com.sandeep.expensetracker.dto.ChangePasswordRequest;
import com.sandeep.expensetracker.dto.UpdateProfileRequest;
import com.sandeep.expensetracker.dto.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(String email);
    UserProfileResponse updateProfile(String email, UpdateProfileRequest request);
    void changePassword(String email, ChangePasswordRequest request);
}
