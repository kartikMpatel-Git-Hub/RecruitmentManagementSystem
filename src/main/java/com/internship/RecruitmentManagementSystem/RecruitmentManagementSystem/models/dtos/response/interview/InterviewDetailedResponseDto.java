package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.position.PositionResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate.CandidateResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.InterviewStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class InterviewDetailedResponseDto {
    private Integer interviewId;

    private CandidateResponseDto candidate;

    private PositionResponseDto position;

    private String interviewLink;

    private LocalTime interviewTime;

    private LocalDate interviewDate;

    private LocalTime interviewEndTime;

    private InterviewStatus interviewStatus;

    private Set<InterviewInterviewerResponseDto> interviewers;
}
