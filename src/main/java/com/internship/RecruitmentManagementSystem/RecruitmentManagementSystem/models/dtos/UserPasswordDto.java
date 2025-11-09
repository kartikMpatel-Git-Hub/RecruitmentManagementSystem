package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class UserPasswordDto {

    @NotEmpty(message = "Current Password Can't Be Empty !!")
    private String currentPassword;

    @NotEmpty(message = "New Password Can't Be Empty !!")
    private String newPassword;

}