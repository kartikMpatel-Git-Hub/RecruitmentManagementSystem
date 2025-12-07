package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewCreateDto {

    @NotNull
    private Integer roundId;

    @Size(max = 512)
    private String interviewLink;

    private Integer numberOfInterviewers;

    private List<Integer> interviewerIds;

    private LocalTime interviewTime;

    private LocalTime interviewEndTime;

    private LocalDate interviewDate;

}
