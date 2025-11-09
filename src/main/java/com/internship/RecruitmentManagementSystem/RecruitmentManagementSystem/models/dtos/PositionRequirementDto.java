package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.Requirement;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.PositionModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Deprecated
public class PositionRequirementDto {

    private Integer positionRequirementId;

    private SkillDto positionSkill;

    private Integer position;

    private Requirement positionRequirement;
}
