package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecentCandidateDto {

    private Integer candidateId;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhoneNumber;
    private List<String> primarySkills;
    private Integer experienceYears;
    private LocalDateTime createdAt;
}
