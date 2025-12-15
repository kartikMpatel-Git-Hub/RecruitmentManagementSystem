package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter;

import lombok.Data;

@Data
public class RecruiterSummaryStatsDto {
    private long totalPositions;
    private long activePositions;
    private long totalApplications;
    private long interviewsScheduled;
    private long candidatesSelected;
}
