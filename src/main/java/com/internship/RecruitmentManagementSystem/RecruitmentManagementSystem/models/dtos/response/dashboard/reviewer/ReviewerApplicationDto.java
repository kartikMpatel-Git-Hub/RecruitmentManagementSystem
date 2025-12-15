package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.reviewer;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReviewerApplicationDto {
    private Integer applicationId;
    private String candidateName;
    private Integer candidateExperience;
    private String positionTitle;
    private String status;
    private LocalDate appliedDate;
}
