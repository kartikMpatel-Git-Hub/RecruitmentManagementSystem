package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.serviceInterface;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.admin.AdminDashboardResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.hr.HrDashboardResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.interviewer.InterviewerDashboardResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter.RecruiterDashboardResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.reviewer.ReviewerDashboardResponseDto;

public interface DashboardServiceInterface {

    AdminDashboardResponseDto adminDashboardData();
    HrDashboardResponseDto hrDashboardData();
    RecruiterDashboardResponseDto recruiterDashboardData(Integer recruiterId);
    ReviewerDashboardResponseDto reviewerDashboardData(Integer reviewerId);
    InterviewerDashboardResponseDto interviewerDashboardData(Integer interviewerId);

}
