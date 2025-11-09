package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillCreateDto {
    private String skill;
}
