package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.interviewer;

import lombok.Data;

@Data
public class InterviewerSummaryStatsDto {
    private long totalAssignedInterviews;
    private long upcomingInterviews;
    private long interviewsToday;
    private long completedInterviews;
    private long pendingFeedbackCount;
}
