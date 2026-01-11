package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.authentication;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String userType;
}
