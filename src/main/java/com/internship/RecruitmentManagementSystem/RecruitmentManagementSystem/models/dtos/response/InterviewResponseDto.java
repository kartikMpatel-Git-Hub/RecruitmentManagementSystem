package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response;


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

    private String interviewLink;

    private LocalTime interviewTime;

    private LocalDate localDate;

    private InterviewStatus interviewStatus;

    private Set<InterviewInterviewerResponseDto> interviewers;

}
