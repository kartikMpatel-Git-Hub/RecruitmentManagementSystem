package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.hr;

import lombok.Data;

@Data
public class HrSummaryStatsDto {
    private long totalCandidates;
    private long newCandidatesToday;
    private long totalApplications;
    private long totalOpenPositions;
    private long totalDegrees;
    private long totalSkills;
    private long totalUniversities;
    private long upcomingInterviewsCount;
}
