package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class InterviewInterviewerResponseDto {
    private Integer interviewInterviewerId;

    private UserMinimalResponseDto interviewer;

    private InterviewerFeedbackResponseDto interviewerFeedback;
}
