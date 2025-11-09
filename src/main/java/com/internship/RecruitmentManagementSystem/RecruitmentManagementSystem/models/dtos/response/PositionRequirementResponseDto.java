package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.SkillDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Requirement;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PositionRequirementResponseDto {
    private Integer positionRequirementId;

    private SkillResponseDto positionSkill;

    private Integer position;

    private Requirement positionRequirement;
}
