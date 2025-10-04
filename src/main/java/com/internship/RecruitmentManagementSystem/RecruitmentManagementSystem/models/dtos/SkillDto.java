package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private Integer skillId;

    private String skill;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
