package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.interviewer;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyInterviewCountDto {
    private LocalDate date;
    private long interviewCount;
}
