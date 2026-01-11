package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.candidate.skill;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ProficiencyLevel;
import lombok.Data;

@Data
public class CandidateSkillCreateDto {
    private Integer candidate;

    private Integer skill;

    private ProficiencyLevel proficiencyLevel;

    private Integer yearsOfExperience;
}
