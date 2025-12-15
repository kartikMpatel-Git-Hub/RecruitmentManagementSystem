package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.recruiter;

import lombok.Data;

@Data
public class PositionPerformanceDto {
    private Integer positionId;
    private String positionTitle;
    private long applications;
    private double shortlistRate;
    private double mappedRate;
    private double selectionRate;
    private double rejectionRate;
}
