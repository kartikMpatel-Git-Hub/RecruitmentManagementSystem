package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationStatusCountDto {
    private ApplicationStatus applicationStatus;
    private Long count;
}
