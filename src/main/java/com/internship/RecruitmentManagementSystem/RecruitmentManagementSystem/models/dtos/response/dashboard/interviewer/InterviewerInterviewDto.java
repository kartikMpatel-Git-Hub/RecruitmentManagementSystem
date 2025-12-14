package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.interviewer;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class InterviewerInterviewDto {
    private Integer interviewId;
    private String candidateName;
    private String positionTitle;
    private LocalDate interviewDate;
    private LocalTime interviewTime;
    private String interviewType;
    private String interviewStatus;
    private Boolean feedbackSubmitted;
}
