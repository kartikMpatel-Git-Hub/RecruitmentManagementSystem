package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@RequiredArgsConstructor
public class RoundUpdateDto {

    private LocalDate roundExpectedDate;

    private LocalTime roundExpectedTime;

    private Integer roundDurationInMinutes;

    private Integer roundSequence;

    private RoundStatusUpdateDto roundStatus;

}
