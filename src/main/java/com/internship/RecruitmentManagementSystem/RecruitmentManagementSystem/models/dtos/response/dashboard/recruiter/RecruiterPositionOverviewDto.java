package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RecruiterPositionOverviewDto {
    private Integer positionId;
    private String title;
    private String status;
    private long applications;
    private long shortlisted;
    private long selected;
    private long rejected;
    private LocalDateTime createdAt;
}
