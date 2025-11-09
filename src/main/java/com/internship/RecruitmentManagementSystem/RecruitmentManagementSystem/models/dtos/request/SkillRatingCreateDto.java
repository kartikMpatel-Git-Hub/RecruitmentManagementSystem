package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SkillRatingCreateDto {

    @NotNull
    private Integer skillId;

    @Min(0)
    @Max(10)
    private Double skillRating;

    private String skillFeedback;
}
