package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserChangePasswordDto {

        @NotEmpty(message = "Current Password Can't Be Empty !!")
        private String currentPassword;

        @NotEmpty(message = "New Password Can't Be Empty !!")
        private String newPassword;

}
