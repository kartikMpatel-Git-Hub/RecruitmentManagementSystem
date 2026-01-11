package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.register.RegisterUserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user.UserChangePasswordDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.requests.JwtAuthenticationRequest;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.security.JwtTokenHelper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.AuthService;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.FileService;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/authentication")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestBody JwtAuthenticationRequest request) {
        logger.info("Login API called for username: {}", request.getUserName());
        var response = authService.login(request);
        logger.info("Login successful for user: {}", request.getUserName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserModel user,
                                            @RequestBody UserChangePasswordDto request) {
        logger.info("Password change requested by user: {}", user.getUsername());
        logger.debug("Validating old and new password for user: {}", user.getUsername());
        var changedPasswordUser = userService.changePassword(user, request);
        logger.info("Password successfully changed for user: {}", user.getUsername());
        return new ResponseEntity<>(changedPasswordUser, HttpStatus.OK);
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerNewUser(@RequestPart("user") @Valid RegisterUserDto request,
                                             @RequestPart(value = "image") MultipartFile userImage) {
        logger.info("User registration API called for username: {}", request.getUserName());
        var response = userService.registerUser(request,userImage);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/health-check")
    public ResponseEntity<ApiResponse> testMethodForRoleBaseAuthentication() {
        logger.info("Health-check API called by ADMIN");
        ApiResponse response = new ApiResponse(200, "Working!", true);
        logger.debug("Health-check response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
