package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.register.RegisterUserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user.UserChangePasswordDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user.UserCreateDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.requests.JwtAuthenticationRequest;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ErrorResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.JwtAuthenticationResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.security.JwtTokenHelper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.FileService;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/authentication")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private static final String INSUFFICIENT_DATA = "InSufficient Data";
    private static final String USERNAME_REQUIRED = "User Name Not Found!";
    private static final String PASSWORD_REQUIRED = "User Password Not Found!";
    private static final String EMAIL_REQUIRED = "User Email Not Found!";
    private static final String ROLE_REQUIRED = "User Role Not Found!";
    private static final String IMAGE_REQUIRED = "User Image Not Found!";

    private final JwtTokenHelper jwtTokenHelper;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final FileService fileService;

    @Value("${project.image}")
    private String path;

    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestBody JwtAuthenticationRequest request) {
        logger.info("Login API called for username: {}", request.getUserName());

        List<String> errors = validateLoginRequest(request);
        if (!errors.isEmpty()) {
            logger.warn("Login validation failed for user: {} | Errors: {}", request.getUserName(), errors);
            return createErrorResponse(errors);
        }

        logger.debug("Attempting authentication for user: {}", request.getUserName());
        authenticate(request.getUserName(), request.getPassword());

        logger.debug("Loading UserDetails for username: {}", request.getUserName());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUserName());
        var user = userService.getUserByUserName(request.getUserName());

        logger.debug("Generating JWT token for user: {}", request.getUserName());
        String token = jwtTokenHelper.generateToken(userDetails);

        logger.info("Login successful for user: {}", request.getUserName());
        return new ResponseEntity<>(new JwtAuthenticationResponse(
                token, user.getRole().toString()
        ), HttpStatus.OK);
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

        List<String> errors = validateRegistrationRequest(request, userImage);
        if (!errors.isEmpty()) {
            logger.warn("Registration validation failed for user: {} | Errors: {}", request.getUserName(), errors);
            return createErrorResponse(errors);
        }

        try {
            logger.debug("Uploading user image for username: {}", request.getUserName());
            String fileName = fileService.uploadImage(path, userImage);
            logger.debug("Image uploaded successfully: {}", fileName);

            request.setUserImageUrl(fileName);
            logger.debug("Creating user in database for username: {}", request.getUserName());
            var registeredUser = userService.registerUser(request);

            logger.info("User registration successful for username: {}", request.getUserName());
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (InvalidImageFormateException | IOException e) {
            logger.error("User registration failed for username: {} | Reason: {}", request.getUserName(), e.getMessage());
            return createErrorResponse("Invalid image format: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/health-check")
    public ResponseEntity<ApiResponse> testMethodForRoleBaseAuthentication() {
        logger.info("Health-check API called by ADMIN");
        ApiResponse response = new ApiResponse(200, "Working!", true);
        logger.debug("Health-check response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void authenticate(String userName, String password) {
        logger.debug("Authenticating user: {}", userName);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName, password);
        try {
            authenticationManager.authenticate(authToken);
            logger.debug("Authentication successful for user: {}", userName);
        } catch (DisabledException e) {
            logger.error("Authentication failed for user: {} | Reason: Disabled account", userName);
            throw new DisabledException("User Is Disabled!");
        }
    }

    private List<String> validateLoginRequest(JwtAuthenticationRequest request) {
        List<String> errors = new ArrayList<>();
        if (isNullOrEmpty(request.getUserName())) errors.add(USERNAME_REQUIRED);
        if (isNullOrEmpty(request.getPassword())) errors.add(PASSWORD_REQUIRED);
        return errors;
    }

    private List<String> validateRegistrationRequest(RegisterUserDto request, MultipartFile userImage) {
        List<String> errors = new ArrayList<>();
        if (userImage == null || userImage.isEmpty()) errors.add(IMAGE_REQUIRED);
        if (isNullOrEmpty(request.getUserName())) errors.add(USERNAME_REQUIRED);
        if (isNullOrEmpty(request.getUserEmail())) errors.add(EMAIL_REQUIRED);
        if (isNullOrEmpty(request.getUserPassword())) errors.add(PASSWORD_REQUIRED);
        if (isNullOrEmpty(request.getRole())) errors.add(ROLE_REQUIRED);
        return errors;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private ResponseEntity<?> createErrorResponse(String error) {
        logger.debug("Creating error response: {}", error);
        return createErrorResponse(List.of(error));
    }

    private ResponseEntity<?> createErrorResponse(List<String> errors) {
        logger.debug("Error response created with errors: {}", errors);
        return new ResponseEntity<>(new ErrorResponse(400, INSUFFICIENT_DATA, errors, false), HttpStatus.BAD_REQUEST);
    }
}
