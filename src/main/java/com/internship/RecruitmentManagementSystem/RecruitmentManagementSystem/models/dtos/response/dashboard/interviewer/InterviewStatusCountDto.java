package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.interviewer;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.InterviewStatus;
import lombok.Data;

@Data
public class InterviewStatusCountDto {
    private InterviewStatus interviewStatus;
    private long count;
}
