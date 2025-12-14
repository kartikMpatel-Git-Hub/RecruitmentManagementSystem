package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.admin.AdminSummaryStatsDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.hr.HrSummaryStatsDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.interviewer.*;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter.PositionPerformanceDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter.RecruiterPositionOverviewDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter.RecruiterRecentApplicationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter.RecruiterSummaryStatsDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.reviewer.ReviewerApplicationDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.reviewer.ReviewerSummaryStatsDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility.*;

import java.util.List;

public interface DashboardBuilderServiceInterface {
    AdminSummaryStatsDto buildAdminSummaryStats();
    HrSummaryStatsDto buildHrSummaryStats();
    List<ApplicationStatusCountDto> buildApplicationStatusCounts();
    List<RecentCandidateDto> buildRecentCandidates();
    List<UpcomingInterviewDto> buildUpcomingInterviews();
    List<DailyApplicationsCountDto> buildApplicationsPerDay(int days);
    List<SkillCountDto> buildTopSkills();
    ExperienceDistributionDto buildExperienceDistribution();
    List<TopCandidateByExperienceDto> buildTopCandidatesByExperience();
    List<InterviewOutcomeStatDto> buildInterviewOutcomeStats();
    List<DegreeCountDto> buildTopDegrees();
    List<UniversityCountDto> buildTopUniversities();
    List<MonthlyHiringStatDto> buildMonthlyHiringStats();
    List<PositionAnalyticsDto> buildPositionAnalytics();
    RecruitmentFunnelStatDto buildRecruitmentFunnelStat();

    RecruiterSummaryStatsDto buildRecruiterSummaryStats(Integer recruiterId);
    List<RecruiterPositionOverviewDto> buildRecruiterPositionOverviews(Integer recruiterId);
    List<ApplicationStatusCountDto> buildApplicationStatusCounts(Integer recruiterId);
    List<DailyApplicationsCountDto> buildApplicationsPerDay(Integer recruiterId, int i);
    List<RecruiterRecentApplicationDto> buildRecruiterRecentApplications(Integer recruiterId);
    List<UpcomingInterviewDto> buildUpcomingInterviews(Integer recruiterId);
    List<PositionPerformanceDto> buildPositionPerformance(Integer recruiterId);

    ReviewerSummaryStatsDto buildReviewerSummaryStats(Integer reviewerId);
    List<ReviewerApplicationDto> buildRecentApplications();
    List<ReviewerApplicationDto> buildPendingReviewApplications();
    List<ReviewerApplicationDto> buildReviewerShortlistedApplications(Integer reviewerId);

    InterviewerSummaryStatsDto buildInterviewerSummaryStats(Integer interviewerId);
    List<InterviewerInterviewDto> buildTodaysInterviews(Integer interviewerId);
    List<InterviewerInterviewDto> buildUpcomingInterview(Integer interviewerId);
    List<InterviewerInterviewDto> buildPendingFeedbackInterviews(Integer interviewerId);
    List<InterviewerInterviewDto> buildCompleteInterviews(Integer interviewerId);
    List<InterviewStatusCountDto> buildInterviewStats(Integer interviewerId);
    FeedbackStatusStatDto buildFeedbackStatus(Integer interviewerId);

    List<DailyInterviewCountDto> buildNextInterviewPerDay(Integer interviewerId, int i);

    List<DailyInterviewCountDto> buildPreviousInterviewPerDay(Integer interviewerId, int i);
}
