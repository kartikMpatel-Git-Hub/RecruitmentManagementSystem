package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.CredentialException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.InvalidImageFormateException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UserPasswordDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.requests.JwtAuthenticationRequest;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ApiResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.ErrorResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.JwtAuthenticationResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.security.JwtTokenHelper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.FileService;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.UserService;
import jakarta.validation.Valid;
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
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private static final String INSUFFICIENT_DATA = "InSufficient Data";
    private static final String USERNAME_REQUIRED = "User Name Not Found!";
    private static final String PASSWORD_REQUIRED = "User Password Not Found!";
    private static final String EMAIL_REQUIRED = "User Email Not Found!";
    private static final String ROLE_REQUIRED = "User Role Not Found!";
    private static final String IMAGE_REQUIRED = "User Image Not Found!";
    private static final String USER_ID_REQUIRED = "User Id Not Found!";

    private final JwtTokenHelper jwtTokenHelper;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final FileService fileService;

    @Value("${project.image}")
    private String path;


    public AuthenticationController(JwtTokenHelper jwtTokenHelper, UserDetailsService userDetailsService, AuthenticationManager authenticationManager, UserService userService, FileService fileService) {
        this.jwtTokenHelper = jwtTokenHelper;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.fileService = fileService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestBody JwtAuthenticationRequest request) {
        logger.info("Login attempt for user: {}", request.getUserName());

        List<String> errors = validateLoginRequest(request);
        if (!errors.isEmpty()) {
            logger.warn("Login validation failed for user: {}", request.getUserName());
            return createErrorResponse(errors);
        }

        authenticate(request.getUserName(), request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUserName());
        UserDto user = userService.getUserByUserName(request.getUserName());
        String token = jwtTokenHelper.generateToken(userDetails);

        logger.info("Successfully generated token for user: {}", request.getUserName());
        return new ResponseEntity<>(new JwtAuthenticationResponse(
                token,
                user.getRole().toString()
        ), HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserModel user,
            @RequestBody UserPasswordDto request) {
        logger.info("Password Change attempt for user {}",user.getUsername());
        UserDto changedPasswordUser = userService.changePassword(user,request.getCurrentPassword(),request.getNewPassword());
        return new ResponseEntity<>(changedPasswordUser, HttpStatus.OK);
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerNewUser(@RequestPart("user") @Valid UserDto request,
                                             @RequestPart(value = "image",required = true) MultipartFile userImage,
                                           @RequestPart(value = "role", required = true) String userRole) {
        logger.info("Attempting to register new user: {}", request.getUserName());

        List<String> errors = validateRegistrationRequest(request, userImage, userRole);
        if (!errors.isEmpty()) {
            logger.warn("Registration validation failed for user: {}", request.getUserName());
            return createErrorResponse(errors);
        }

        try {
            String fileName = fileService.uploadImage(path, userImage);
            request.setUserImageUrl(fileName);
            UserDto registeredUser = userService.registerUser(request, userRole);
            logger.info("Successfully registered user: {}", request.getUserName());
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (InvalidImageFormateException | IOException e) {
            logger.error("Error updating user: {}", e.getMessage());
            return createErrorResponse("Invalid image format: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/health-check")
    public ResponseEntity<ApiResponse> testMethodForRoleBaseAuthentication() {
        logger.info("Health check requested by admin");
        return new ResponseEntity<>(new ApiResponse(200, "Working!", true), HttpStatus.OK);
    }

    private void authenticate(String userName, String password) {
        logger.debug("Authenticating user: {}", userName);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName, password);
        try {
            authenticationManager.authenticate(authToken);
        } catch (DisabledException exception) {
            logger.error("Authentication failed - user disabled: {}", userName);
            throw new CredentialException("User Is Disabled!");
        }
    }

    private List<String> validateLoginRequest(JwtAuthenticationRequest request) {
        List<String> errors = new ArrayList<>();
        if (isNullOrEmpty(request.getUserName())) errors.add(USERNAME_REQUIRED);
        if (isNullOrEmpty(request.getPassword())) errors.add(PASSWORD_REQUIRED);
        return errors;
    }

    private List<String> validateRegistrationRequest(UserDto request, MultipartFile userImage, String userRole) {
        List<String> errors = new ArrayList<>();
        if (userImage == null || userImage.isEmpty()) errors.add(IMAGE_REQUIRED);
        if (isNullOrEmpty(request.getUserName())) errors.add(USERNAME_REQUIRED);
        if (isNullOrEmpty(request.getUserEmail())) errors.add(EMAIL_REQUIRED);
        if (isNullOrEmpty(request.getUserPassword())) errors.add(PASSWORD_REQUIRED);
        if (isNullOrEmpty(userRole)) errors.add(ROLE_REQUIRED);
        return errors;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private ResponseEntity<?> createErrorResponse(String error) {
        return createErrorResponse(List.of(error));
    }

    private ResponseEntity<?> createErrorResponse(List<String> errors) {
        return new ResponseEntity<>(
                new ErrorResponse(400, INSUFFICIENT_DATA, errors, false),
                HttpStatus.BAD_REQUEST);
    }
}
