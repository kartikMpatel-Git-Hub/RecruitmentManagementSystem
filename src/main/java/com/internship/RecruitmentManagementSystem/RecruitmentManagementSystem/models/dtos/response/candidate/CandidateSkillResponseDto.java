package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.candidate;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill.SkillResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ProficiencyLevel;
import lombok.Data;

@Data
public class CandidateSkillResponseDto {
    private Integer candidateSkillId;

    private SkillResponseDto skill;

    private ProficiencyLevel proficiencyLevel;

    private Integer yearsOfExperience;
}
