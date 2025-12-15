package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.register;

import lombok.Data;

@Data
public class RegisterUserResponseDto {
    private Integer registerId;

    private String userName;

    private String userEmail;

    private String userImageUrl;

    private String role;
}
