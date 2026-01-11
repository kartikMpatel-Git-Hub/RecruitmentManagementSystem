package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.register.RegisterUserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ErrorResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.RegisterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RegisterRequestController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterRequestController.class);
    private final RegisterService registerService;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @DeleteMapping("/{registerId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer registerId) {
        logger.info("Attempting to Reject Request with ID: {}", registerId);

        if (registerId == null) {
            logger.warn("Reject Request attempt failed: Register ID is null");
            return createErrorResponse(List.of("Register Id Requires"));
        }

        registerService.rejectRequest(registerId);
        logger.info("Successfully Reject Request with ID: {}", registerId);
        return ResponseEntity.ok(new ApiResponse(200, "Reject Request Successfully", true));
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ResponseEntity<PaginatedResponse<RegisterUserResponseDto>> getAllRegisterRequest(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "30") Integer size,
            @RequestParam(defaultValue = "registerId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        logger.info("Fetching all users (page: {}, size: {}, sortBy: {}, sortDir: {})", page, size, sortBy, sortDir);
        return ResponseEntity.ok(registerService.getAllRequest(page, size, sortBy, sortDir));
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{registerId}")
    public ResponseEntity<UserResponseDto> acceptRequest(@PathVariable Integer registerId) {
        logger.info("Fetching user profile by ID: {}", registerId);
        var res = registerService.acceptRequest(registerId);
        return ResponseEntity.ok(res);
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/count")
    public ResponseEntity<Long> countRequest() {
        var res = registerService.countRequest();
        return ResponseEntity.ok(res);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(List<String> errors) {
        return ResponseEntity.badRequest().body(new ErrorResponse(400, "Insufficient Data", errors, false));
    }
}
