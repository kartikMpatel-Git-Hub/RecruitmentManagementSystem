package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.exception.exceptions;

public class JwtAuthenticationException extends RuntimeException {
    public JwtAuthenticationException(String message) {
        super(message);
    }
}
