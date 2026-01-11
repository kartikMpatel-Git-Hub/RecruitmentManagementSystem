package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
public class UserMinimalResponseDto {

    private Integer userId;

    private String username;

    private String userEmail;

    private String userImageUrl;

}
