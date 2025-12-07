package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserMinimalResponseDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class InterviewInterviewerResponseDto {
    private Integer interviewInterviewerId;

    private UserMinimalResponseDto interviewer;

    private InterviewerFeedbackResponseDto interviewerFeedback;
}
