package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class UpcomingInterviewDto {
    private Integer interviewId;
    private String candidateName;
    private String interviewerName;
    private LocalDate interviewDate;
    private LocalTime interviewTime;
    private String meetingLink;
}
