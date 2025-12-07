package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.position;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.skill.SkillGetDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Requirement;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PositionRequirementUpdateDto {
    private SkillGetDto positionSkill;

    private Requirement positionRequirement;
}
