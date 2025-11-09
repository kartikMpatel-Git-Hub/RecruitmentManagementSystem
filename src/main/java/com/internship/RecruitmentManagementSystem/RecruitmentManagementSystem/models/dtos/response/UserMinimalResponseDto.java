package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


@Data
@RequiredArgsConstructor
public class UserMinimalResponseDto {

    private Integer userId;

    private String userName;

    private String userEmail;

    private String userImageUrl;

}
