package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.role.RoleResponseDto;

import java.time.LocalDateTime;

public class UserFullResponseDto {
    private Integer userId;

    private String userName;

    private String userEmail;

    private String userImageUrl;

    private RoleResponseDto role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean userEnabled = true;
}
