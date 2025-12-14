package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.hr;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility.*;
import lombok.Data;

import java.util.List;

@Data
public class HrDashboardResponseDto {
    private HrSummaryStatsDto summaryStats;
    private List<ApplicationStatusCountDto> applicationStatusCounts;
    private List<RecentCandidateDto> recentCandidates;
    private List<UpcomingInterviewDto> upcomingInterviews;
    private List<DailyApplicationsCountDto> applicationsPerDayLast7Days;
    private List<DailyApplicationsCountDto> applicationsPerDayLast15Days;
    private List<SkillCountDto> topSkills;
    private List<DegreeCountDto> topDegrees;
    private List<UniversityCountDto> topUniversities;
    private ExperienceDistributionDto experienceDistribution;
    private List<InterviewOutcomeStatDto> interviewOutcomeStats;
    private List<TopCandidateByExperienceDto> topCandidatesByExperience;

    private RecruitmentFunnelStatDto  recruitmentFunnelStat;
    private List<PositionAnalyticsDto> positionAnalytics;
    List<MonthlyHiringStatDto> monthlyHiringStats;
}
