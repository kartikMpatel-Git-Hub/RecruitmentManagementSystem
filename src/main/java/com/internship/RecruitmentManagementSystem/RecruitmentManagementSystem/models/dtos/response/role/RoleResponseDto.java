package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.role;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RoleResponseDto {
    private Integer roleId;

    private String role;
}
