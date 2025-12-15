package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyApplicationsCountDto {
    private LocalDate date;
    private long count;
}
