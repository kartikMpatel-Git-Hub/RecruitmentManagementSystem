package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility.*;
import lombok.Data;
import java.util.List;

@Data
public class RecruiterDashboardResponseDto {

    private RecruiterSummaryStatsDto summaryStats;

    private List<RecruiterPositionOverviewDto> positionsOverview;

    private List<ApplicationStatusCountDto> applicationStatusCounts;

    private List<DailyApplicationsCountDto> applicationsLast7Days;

    private List<DailyApplicationsCountDto> applicationsLast15Days;

    private List<RecruiterRecentApplicationDto> recentApplications;

    private List<UpcomingInterviewDto> upcomingInterviews;

    private List<PositionPerformanceDto> positionPerformance;
}
