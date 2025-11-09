package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class InterviewerFeedbackResponseDto {
    private Integer interviewFeedbackId;
    private String interviewFeedback;
    private List<SkillRatingResponseDto> skillRatings;
}
