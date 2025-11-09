package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.SkillDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.SkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Requirement;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PositionRequirementUpdateDto {
    private SkillGetDto positionSkill;

    private Requirement positionRequirement;
}
