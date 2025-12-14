package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.dashboard.utility;

import lombok.Data;

import java.util.List;

@Data
public class TopCandidateByExperienceDto {
    private Integer candidateId;
    private String name;
    private String email;
    private Integer experienceYears;
    private List<String> primarySkills;
}
