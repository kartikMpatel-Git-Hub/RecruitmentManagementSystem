package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility;

import lombok.Data;

@Data
public class PositionAnalyticsDto {
    private Integer positionId;
    private String title;
    private long totalApplications;
    private long shortlisted;
    private long selected;
}
