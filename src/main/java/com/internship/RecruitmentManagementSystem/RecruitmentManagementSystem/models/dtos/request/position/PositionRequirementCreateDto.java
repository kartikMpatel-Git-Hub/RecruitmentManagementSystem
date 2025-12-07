package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.position;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.SkillDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Requirement;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PositionRequirementCreateDto {
    private SkillDto positionSkill;

    private Requirement positionRequirement;
}
