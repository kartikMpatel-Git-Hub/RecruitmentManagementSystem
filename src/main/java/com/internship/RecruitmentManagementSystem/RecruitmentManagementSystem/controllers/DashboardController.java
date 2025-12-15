package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.controllers;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.admin.AdminDashboardResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.hr.HrDashboardResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.UserModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardResponseDto> getAdminDashboardData() {
        var response = dashboardService.adminDashboardData();
        return new ResponseEntity<>(response,
                HttpStatus.OK);
    }

    @GetMapping("/hr")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<HrDashboardResponseDto> getHrDashboardData() {
        var response = dashboardService.hrDashboardData();
        return new ResponseEntity<>(response,
                HttpStatus.OK);
    }

    @GetMapping("/recruiter")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<?> getRecruiterDashboardData() {
        UserModel currentUser = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer recruiterId = currentUser.getUserId();
        var response = dashboardService.recruiterDashboardData(recruiterId);
        return new ResponseEntity<>(response,
                HttpStatus.OK);
    }

    @GetMapping("/reviewer")
    @PreAuthorize("hasRole('REVIEWER')")
    public ResponseEntity<?> getReviewerDashboardData() {
        UserModel currentUser = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer recruiterId = currentUser.getUserId();
        var response = dashboardService.reviewerDashboardData(recruiterId);
        return new ResponseEntity<>(response,
                HttpStatus.OK);
    }

    @GetMapping("/interviewer")
    @PreAuthorize("hasRole('INTERVIEWER')")
    public ResponseEntity<?> getInterviewerDashboardData() {
        UserModel currentUser = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer recruiterId = currentUser.getUserId();
        var response = dashboardService.interviewerDashboardData(recruiterId);
        return new ResponseEntity<>(response,
                HttpStatus.OK);
    }
}
