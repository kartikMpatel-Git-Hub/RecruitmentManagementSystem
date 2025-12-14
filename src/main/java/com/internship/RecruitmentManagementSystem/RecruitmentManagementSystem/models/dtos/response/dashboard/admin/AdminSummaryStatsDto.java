package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.admin;

import lombok.Data;

@Data
public class AdminSummaryStatsDto {
    private long totalCandidates;
    private long newCandidatesToday;
    private long totalApplications;
    private long totalUsers;
    private long totalOpenPositions;
    private long totalDegrees;
    private long totalSkills;
    private long totalUniversities;
    private long upcomingInterviewsCount;

}
