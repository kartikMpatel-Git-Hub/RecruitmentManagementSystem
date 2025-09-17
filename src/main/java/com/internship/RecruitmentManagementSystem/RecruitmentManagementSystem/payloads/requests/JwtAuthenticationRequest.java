package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.requests;

import lombok.Data;

@Data
public class JwtAuthenticationRequest {

    private String userName;
    private String password;

}
