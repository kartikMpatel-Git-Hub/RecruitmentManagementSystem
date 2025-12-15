package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.register;

import lombok.Data;

@Data
public class RegisterUserDto {
    private String userName;

    private String userPassword;

    private String userEmail;

    private String role;

    private String userImageUrl;
}
