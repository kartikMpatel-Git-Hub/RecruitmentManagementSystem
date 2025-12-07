package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SkillResponseDto {
    private Integer skillId;

    private String skill;
}
