package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Deprecated
public class RoleDto {

    private Integer roleId;

    private String role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
