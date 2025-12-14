package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.admin.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.hr.HrDashboardResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.hr.HrSummaryStatsDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.interviewer.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.reviewer.ReviewerApplicationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.reviewer.ReviewerDashboardResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.reviewer.ReviewerSummaryStatsDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface.DashboardServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService implements DashboardServiceInterface {

    private final DashboardBuilderService dashboardBuilderService;

    @Override
    public AdminDashboardResponseDto adminDashboardData() {
        AdminDashboardResponseDto dto = new AdminDashboardResponseDto();
        AdminSummaryStatsDto summaryStatsDto = dashboardBuilderService.buildAdminSummaryStats();
        List<ApplicationStatusCountDto> applicationStatusCounts = dashboardBuilderService.buildApplicationStatusCounts();
        List<RecentCandidateDto> recentCandidates = dashboardBuilderService.buildRecentCandidates();
        List<UpcomingInterviewDto> upcomingInterviews = dashboardBuilderService.buildUpcomingInterviews();
        List<DailyApplicationsCountDto> applicationsPerDayLast7Days = dashboardBuilderService.buildApplicationsPerDay(7);
        List<DailyApplicationsCountDto> applicationsPerDayLast15Days = dashboardBuilderService.buildApplicationsPerDay(15);
        List<SkillCountDto> topSkills = dashboardBuilderService.buildTopSkills();
        List<DegreeCountDto> topDegrees = dashboardBuilderService.buildTopDegrees();
        List<UniversityCountDto> topUniversities = dashboardBuilderService.buildTopUniversities();
        ExperienceDistributionDto experienceDistribution = dashboardBuilderService.buildExperienceDistribution();
        List<InterviewOutcomeStatDto> interviewOutcomeStats = dashboardBuilderService.buildInterviewOutcomeStats();
        List<TopCandidateByExperienceDto> topCandidatesByExperience = dashboardBuilderService.buildTopCandidatesByExperience();
        RecruitmentFunnelStatDto recruitmentFunnelStat = dashboardBuilderService.buildRecruitmentFunnelStat();
        List<MonthlyHiringStatDto> monthlyHiringStats = dashboardBuilderService.buildMonthlyHiringStats();
        List<PositionAnalyticsDto> positionAnalytics = dashboardBuilderService.buildPositionAnalytics();

        dto.setSummaryStats(summaryStatsDto);
        dto.setApplicationStatusCounts(applicationStatusCounts);
        dto.setRecentCandidates(recentCandidates);
        dto.setUpcomingInterviews(upcomingInterviews);
        dto.setApplicationsPerDayLast7Days(applicationsPerDayLast7Days);
        dto.setApplicationsPerDayLast15Days(applicationsPerDayLast15Days);
        dto.setTopSkills(topSkills);
        dto.setTopDegrees(topDegrees);
        dto.setTopUniversities(topUniversities);
        dto.setExperienceDistribution(experienceDistribution);
        dto.setInterviewOutcomeStats(interviewOutcomeStats);
        dto.setTopCandidatesByExperience(topCandidatesByExperience);
        dto.setRecruitmentFunnelStat(recruitmentFunnelStat);
        dto.setPositionAnalytics(positionAnalytics);
        dto.setMonthlyHiringStats(monthlyHiringStats);

        return dto;
    }

    @Override
    public HrDashboardResponseDto hrDashboardData() {
        HrDashboardResponseDto dto = new HrDashboardResponseDto();
        HrSummaryStatsDto summaryStatsDto = dashboardBuilderService.buildHrSummaryStats();
        List<ApplicationStatusCountDto> applicationStatusCounts = dashboardBuilderService.buildApplicationStatusCounts();
        List<RecentCandidateDto> recentCandidates = dashboardBuilderService.buildRecentCandidates();
        List<UpcomingInterviewDto> upcomingInterviews = dashboardBuilderService.buildUpcomingInterviews();
        List<DailyApplicationsCountDto> applicationsPerDayLast7Days = dashboardBuilderService.buildApplicationsPerDay(7);
        List<DailyApplicationsCountDto> applicationsPerDayLast15Days = dashboardBuilderService.buildApplicationsPerDay(15);
        List<SkillCountDto> topSkills = dashboardBuilderService.buildTopSkills();
        List<DegreeCountDto> topDegrees = dashboardBuilderService.buildTopDegrees();
        List<UniversityCountDto> topUniversities = dashboardBuilderService.buildTopUniversities();
        ExperienceDistributionDto experienceDistribution = dashboardBuilderService.buildExperienceDistribution();
        List<InterviewOutcomeStatDto> interviewOutcomeStats = dashboardBuilderService.buildInterviewOutcomeStats();
        List<TopCandidateByExperienceDto> topCandidatesByExperience = dashboardBuilderService.buildTopCandidatesByExperience();
        RecruitmentFunnelStatDto recruitmentFunnelStat = dashboardBuilderService.buildRecruitmentFunnelStat();
        List<PositionAnalyticsDto> positionAnalytics = dashboardBuilderService.buildPositionAnalytics();
        List<MonthlyHiringStatDto> monthlyHiringStats = dashboardBuilderService.buildMonthlyHiringStats();

        dto.setSummaryStats(summaryStatsDto);
        dto.setApplicationStatusCounts(applicationStatusCounts);
        dto.setRecentCandidates(recentCandidates);
        dto.setUpcomingInterviews(upcomingInterviews);
        dto.setApplicationsPerDayLast7Days(applicationsPerDayLast7Days);
        dto.setApplicationsPerDayLast15Days(applicationsPerDayLast15Days);
        dto.setTopSkills(topSkills);
        dto.setTopDegrees(topDegrees);
        dto.setTopUniversities(topUniversities);
        dto.setExperienceDistribution(experienceDistribution);
        dto.setInterviewOutcomeStats(interviewOutcomeStats);
        dto.setTopCandidatesByExperience(topCandidatesByExperience);
        dto.setRecruitmentFunnelStat(recruitmentFunnelStat);
        dto.setPositionAnalytics(positionAnalytics);
        dto.setMonthlyHiringStats(monthlyHiringStats);
        return dto;
    }

    @Override
    public RecruiterDashboardResponseDto recruiterDashboardData(Integer recruiterId) {
        RecruiterDashboardResponseDto dto = new RecruiterDashboardResponseDto();

        RecruiterSummaryStatsDto summaryStatsDto = dashboardBuilderService.buildRecruiterSummaryStats(recruiterId);
        List<RecruiterPositionOverviewDto> positionOverviews = dashboardBuilderService.buildRecruiterPositionOverviews(recruiterId);
        List<ApplicationStatusCountDto> applicationStatusCounts = dashboardBuilderService.buildApplicationStatusCounts(recruiterId);
        List<DailyApplicationsCountDto> applicationsPerDayLast7Days = dashboardBuilderService.buildApplicationsPerDay(recruiterId,7);
        List<DailyApplicationsCountDto> applicationsPerDayLast15Days = dashboardBuilderService.buildApplicationsPerDay(recruiterId,15);
        List<RecruiterRecentApplicationDto> recentApplications = dashboardBuilderService.buildRecruiterRecentApplications(recruiterId);
        List<UpcomingInterviewDto> upcomingInterviews = dashboardBuilderService.buildUpcomingInterviews(recruiterId);
        List<PositionPerformanceDto> positionAnalytics = dashboardBuilderService.buildPositionPerformance(recruiterId);

        dto.setSummaryStats(summaryStatsDto);
        dto.setPositionsOverview(positionOverviews);
        dto.setApplicationStatusCounts(applicationStatusCounts);
        dto.setApplicationsLast7Days(applicationsPerDayLast7Days);
        dto.setApplicationsLast15Days(applicationsPerDayLast15Days);
        dto.setRecentApplications(recentApplications);
        dto.setUpcomingInterviews(upcomingInterviews);
        dto.setPositionPerformance(positionAnalytics);

        return dto;
    }

    @Override
    public ReviewerDashboardResponseDto reviewerDashboardData(Integer reviewerId) {

        ReviewerDashboardResponseDto dto = new ReviewerDashboardResponseDto();

        ReviewerSummaryStatsDto reviewerSummaryStats = dashboardBuilderService.buildReviewerSummaryStats(reviewerId);
        List<ApplicationStatusCountDto> applicationStatusCount = dashboardBuilderService.buildApplicationStatusCounts();
        List<ReviewerApplicationDto> recentApplications = dashboardBuilderService.buildRecentApplications();
        List<ReviewerApplicationDto> pendingReviewApplications = dashboardBuilderService.buildPendingReviewApplications();
        List<ReviewerApplicationDto> shortlistedApplications = dashboardBuilderService.buildReviewerShortlistedApplications(reviewerId);
        List<DailyApplicationsCountDto> applicationsPerDayLast7Days = dashboardBuilderService.buildApplicationsPerDay(7);
        List<DailyApplicationsCountDto> applicationsPerDayLast15Days = dashboardBuilderService.buildApplicationsPerDay(15);

        dto.setSummaryStats(reviewerSummaryStats);
        dto.setApplicationStatusCounts(applicationStatusCount);
        dto.setRecentApplications(recentApplications);
        dto.setPendingReviewApplications(pendingReviewApplications);
        dto.setShortlistedApplications(shortlistedApplications);
        dto.setApplicationsPerDayLast7Days(applicationsPerDayLast7Days);
        dto.setApplicationsPerDayLast15Days(applicationsPerDayLast15Days);

        return dto;
    }

    @Override
    public InterviewerDashboardResponseDto interviewerDashboardData(Integer interviewerId) {

        InterviewerDashboardResponseDto dto = new InterviewerDashboardResponseDto();

        InterviewerSummaryStatsDto interviewerSummaryStats = dashboardBuilderService.buildInterviewerSummaryStats(interviewerId);
        List<InterviewerInterviewDto> todaysInterviews = dashboardBuilderService.buildTodaysInterviews(interviewerId);
        List<InterviewerInterviewDto> upcomingInterviews = dashboardBuilderService.buildUpcomingInterview(interviewerId);
        List<InterviewerInterviewDto> pendingFeedbackInterviews = dashboardBuilderService.buildPendingFeedbackInterviews(interviewerId);
        List<InterviewerInterviewDto> completedInterviews = dashboardBuilderService.buildCompleteInterviews(interviewerId);
        List<DailyInterviewCountDto> interviewsPerNextNDays = dashboardBuilderService.buildNextInterviewPerDay(interviewerId,7);
        List<DailyInterviewCountDto> interviewsPerPrevNDays = dashboardBuilderService.buildPreviousInterviewPerDay(interviewerId,7);
        List<InterviewStatusCountDto> interviewStatusCounts = dashboardBuilderService.buildInterviewStats(interviewerId);
        FeedbackStatusStatDto feedbackStatus = dashboardBuilderService.buildFeedbackStatus(interviewerId);

        dto.setSummaryStats(interviewerSummaryStats);
        dto.setInterviewsPerNext7Days(interviewsPerNextNDays);
        dto.setInterviewsPerPrevious7Days(interviewsPerPrevNDays);
        dto.setInterviewStatusCounts(interviewStatusCounts);
        dto.setFeedbackStatus(feedbackStatus);
        dto.setTodaysInterviews(todaysInterviews);
        dto.setUpcomingInterviews(upcomingInterviews);
        dto.setPendingFeedbackInterviews(pendingFeedbackInterviews);
        dto.setCompletedInterviews(completedInterviews);

        return dto;
    }


}
