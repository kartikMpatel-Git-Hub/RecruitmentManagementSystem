package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user;

import lombok.Data;

@Data
public class NewUserDto {
    private String userName;
    private String userEmail;
    private String userRole;
}
