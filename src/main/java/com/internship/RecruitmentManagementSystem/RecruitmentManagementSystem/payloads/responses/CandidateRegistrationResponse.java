package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses;

import lombok.*;

@Getter @Setter @AllArgsConstructor
public class CandidateRegistrationResponse {
    private Integer candidateId;
    private Integer userId;
    private String userEmail;
    private String role;
}
