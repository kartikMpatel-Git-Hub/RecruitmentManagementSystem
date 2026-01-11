package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.InterviewStatus;
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
public class InterviewUpdateDto {

    @Size(max = 512)
    private String interviewLink;

    private LocalDate interviewDate;

    private InterviewStatus interviewStatus;

    private LocalTime interviewTime;

    private LocalTime interviewEndTime;

}
