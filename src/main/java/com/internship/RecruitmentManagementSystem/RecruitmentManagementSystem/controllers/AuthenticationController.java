package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions.CredentialException;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.UserDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.requests.JwtAuthenticationRequest;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.JwtAuthenticationResponse;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.security.JwtTokenHelper;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private final JwtTokenHelper jwtTokenHelper;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthenticationController(JwtTokenHelper jwtTokenHelper, UserDetailsService userDetailsService, AuthenticationManager authenticationManager, UserService userService) {
        this.jwtTokenHelper = jwtTokenHelper;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> createToken(@RequestBody JwtAuthenticationRequest request){
        authenticate(request.getUserName(),request.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUserName());
        String token = jwtTokenHelper.generateToken(userDetails);
        JwtAuthenticationResponse response = new JwtAuthenticationResponse();
        response.setToken(token);
        return new ResponseEntity<JwtAuthenticationResponse>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerNewUser(@RequestBody UserDto request){
        UserDto registeredUser = userService.registerUser(request);
        return new ResponseEntity<>(registeredUser,HttpStatus.CREATED);
    }

    private void authenticate(String userName, String password) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userName,password);
        try {
            authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        }
        catch (DisabledException exception){
            throw new CredentialException("User Is Disable !");
        }
    }

}
