package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.user;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.role.RoleResponseDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class UserUpdateDto {
    private Integer userId;

    private String userName;

    private String userEmail;

    private String userImageUrl;

    private RoleResponseDto role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean userEnabled = true;
}
