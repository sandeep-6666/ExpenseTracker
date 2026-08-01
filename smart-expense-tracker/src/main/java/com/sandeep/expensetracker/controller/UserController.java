package com.sandeep.expensetracker.controller;

import com.sandeep.expensetracker.dto.ChangePasswordRequest;
import com.sandeep.expensetracker.dto.UpdateProfileRequest;
import com.sandeep.expensetracker.dto.UserProfileResponse;
import com.sandeep.expensetracker.response.ApiResponse;
import com.sandeep.expensetracker.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Profile and settings endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", userService.getProfile(auth.getName())));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            Authentication auth, @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", userService.updateProfile(auth.getName(), request)));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication auth, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(auth.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}
