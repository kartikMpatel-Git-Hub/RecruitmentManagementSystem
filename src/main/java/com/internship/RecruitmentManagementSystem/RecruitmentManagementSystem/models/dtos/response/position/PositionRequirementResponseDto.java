package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.position;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill.SkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Requirement;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PositionRequirementResponseDto {
    private Integer positionRequirementId;

    private SkillResponseDto positionSkill;

    private Integer position;

    private Integer minYearsOfExperience;

    private Requirement positionRequirement;
}
