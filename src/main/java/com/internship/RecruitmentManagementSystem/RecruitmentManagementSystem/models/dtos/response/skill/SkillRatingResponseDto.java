package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SkillRatingResponseDto {
    private Integer skillRatingId;
    private SkillResponseDto skill;
    private Double skillRating;
    private String skillFeedback;
}
