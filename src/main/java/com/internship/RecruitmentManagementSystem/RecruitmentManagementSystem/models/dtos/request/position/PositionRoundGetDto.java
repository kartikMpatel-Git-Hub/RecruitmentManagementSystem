package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.position;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@RequiredArgsConstructor
public class PositionRoundGetDto {
    private RoundType positionRoundType;

    private Integer positionRoundSequence;

    private LocalDate positionRoundExpectedDate;

    private LocalTime positionRoundExpectedTime;
}
