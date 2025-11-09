package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Deprecated
public class SkillDto {
    private Integer skillId;

    private String skill;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
