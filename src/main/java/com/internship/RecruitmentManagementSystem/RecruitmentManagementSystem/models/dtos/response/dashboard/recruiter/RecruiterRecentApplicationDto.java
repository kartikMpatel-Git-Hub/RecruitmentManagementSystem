package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RecruiterRecentApplicationDto {
    private Integer applicationId;
    private String candidateName;
    private String positionTitle;
    private LocalDate appliedDate;
    private String status;
}
