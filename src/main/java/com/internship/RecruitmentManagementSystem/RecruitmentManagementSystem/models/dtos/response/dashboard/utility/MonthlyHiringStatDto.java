package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility;

import lombok.Data;

@Data
public class MonthlyHiringStatDto {
    private Integer month;
    private long hires;
}
