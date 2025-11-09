package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class InterviewerFeedbackCreateDto {

    @Size(max=500)
    private String interviewFeedback;
    private List<SkillRatingCreateDto> skillRatings;
}
