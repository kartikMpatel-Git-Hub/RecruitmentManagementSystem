package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.reviewer;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility.ApplicationStatusCountDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility.DailyApplicationsCountDto;
import lombok.Data;

import java.util.List;

@Data
public class ReviewerDashboardResponseDto {
    private ReviewerSummaryStatsDto summaryStats;
    private List<ApplicationStatusCountDto> applicationStatusCounts;
    private List<ReviewerApplicationDto> recentApplications;
    private List<ReviewerApplicationDto> pendingReviewApplications;
    private List<ReviewerApplicationDto> shortlistedApplications;
    private List<DailyApplicationsCountDto> applicationsPerDayLast7Days;
    private List<DailyApplicationsCountDto> applicationsPerDayLast15Days;
}

