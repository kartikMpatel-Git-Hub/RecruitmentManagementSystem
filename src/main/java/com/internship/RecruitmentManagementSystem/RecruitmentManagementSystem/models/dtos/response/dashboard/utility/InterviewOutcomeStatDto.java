package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.InterviewStatus;
import lombok.Data;

@Data
public class InterviewOutcomeStatDto {
    private InterviewStatus outcome;
    private long count;
}
