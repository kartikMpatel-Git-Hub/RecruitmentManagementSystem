package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview;


import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.InterviewStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class InterviewResponseDto {

    private Integer interviewId;

    private Integer roundId;

    private Integer candidateId;

    private Integer positionId;

    private Integer applicationId;

    private String interviewLink;

    private LocalTime interviewTime;

    private LocalDate interviewDate;

    private LocalTime interviewEndTime;

    private InterviewStatus interviewStatus;

    private Set<InterviewInterviewerResponseDto> interviewers;

}
