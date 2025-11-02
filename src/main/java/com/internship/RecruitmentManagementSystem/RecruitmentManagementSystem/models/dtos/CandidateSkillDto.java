package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.ProficiencyLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateSkillDto {

    private Integer candidateSkillId;
    private Integer candidate;
    private String candidateName;

    private Integer skill;
    private String skillName;

    private ProficiencyLevel proficiencyLevel;

    private Integer yearsOfExperience;
}
