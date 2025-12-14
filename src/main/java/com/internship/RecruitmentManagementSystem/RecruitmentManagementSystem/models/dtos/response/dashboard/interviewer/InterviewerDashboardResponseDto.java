package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.interviewer;

import lombok.Data;

import java.util.List;

@Data
public class InterviewerDashboardResponseDto {
    private InterviewerSummaryStatsDto summaryStats;

    private List<DailyInterviewCountDto> interviewsPerNext7Days;
    private List<DailyInterviewCountDto> interviewsPerPrevious7Days;
    private List<InterviewStatusCountDto> interviewStatusCounts;
    private FeedbackStatusStatDto feedbackStatus;

    private List<InterviewerInterviewDto> todaysInterviews;
    private List<InterviewerInterviewDto> upcomingInterviews;
    private List<InterviewerInterviewDto> pendingFeedbackInterviews;
    private List<InterviewerInterviewDto> completedInterviews;
}

