package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.reviewer;

import lombok.Data;

@Data
public class ReviewerSummaryStatsDto {
    private long totalApplications;
    private long shortlistedApplications;
    private long pendingApplications;
    private long reviewedToday;
}
