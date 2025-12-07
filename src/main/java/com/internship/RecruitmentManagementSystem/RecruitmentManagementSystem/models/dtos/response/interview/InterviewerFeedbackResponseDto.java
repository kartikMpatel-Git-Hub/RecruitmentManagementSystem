package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.interview;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.skill.SkillRatingResponseDto;
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
