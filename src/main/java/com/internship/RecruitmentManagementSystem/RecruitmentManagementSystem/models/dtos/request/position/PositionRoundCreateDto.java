package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.position;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PositionRoundCreateDto {

    private RoundType positionRoundType;

    private Integer positionRoundSequence;

}
