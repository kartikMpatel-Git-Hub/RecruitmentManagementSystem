package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.position;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@RequiredArgsConstructor
public class PositionRoundResponseDto {

    private Integer positionRoundId;

    private RoundType positionRoundType;

    private Integer positionRoundSequence;

}
