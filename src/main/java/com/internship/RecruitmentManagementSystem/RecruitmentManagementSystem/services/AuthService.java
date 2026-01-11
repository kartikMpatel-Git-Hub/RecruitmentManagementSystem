package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.RegisterException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.ResourceNotFoundException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.requests.JwtAuthenticationRequest;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.JwtAuthenticationResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.UserRepository;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.security.JwtTokenHelper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.AuthServiceInterface;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.ModelServiceInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthServiceInterface {

    private final UserRepository userRepository;
    private static final String USERNAME_REQUIRED = "User Name Not Found!";
    private static final String PASSWORD_REQUIRED = "User Password Not Found!";

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenHelper jwtTokenHelper;
    private final ModelServiceInterface modelService;

    public UserModel getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails userDetails)) {
            throw new ResourceNotFoundException("USER", "Authentication", "No authenticated user found");
        }
        return userRepository.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("USER", "userName", userDetails.getUsername()));

    }

    @Override
    public JwtAuthenticationResponse login(JwtAuthenticationRequest request) {
        List<String> errors = validateLoginRequest(request);
        if (!errors.isEmpty()) {
            logger.warn("Login validation failed for user: {} | Errors: {}", request.getUserName(), errors);
            throw new RegisterException(errors);
        }

        logger.debug("Attempting authentication for user: {}", request.getUserName());
        authenticate(request.getUserName(), request.getPassword());

        logger.debug("Loading UserDetails for username: {}", request.getUserName());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUserName());
        var user = modelService.getUser(userDetails.getUsername());
        logger.debug("Generating JWT token for user: {}", request.getUserName());
        String token = jwtTokenHelper.generateToken(userDetails);
        return new JwtAuthenticationResponse(token,user.getRole().getRole());
    }


    private List<String> validateLoginRequest(JwtAuthenticationRequest request) {
        List<String> errors = new ArrayList<>();
        if (isNullOrEmpty(request.getUserName())) errors.add(USERNAME_REQUIRED);
        if (isNullOrEmpty(request.getPassword())) errors.add(PASSWORD_REQUIRED);
        return errors;
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

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}

