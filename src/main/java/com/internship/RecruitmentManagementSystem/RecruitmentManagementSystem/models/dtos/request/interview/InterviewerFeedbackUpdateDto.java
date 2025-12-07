package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.interview;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.skill.SkillRatingUpdateDto;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class InterviewerFeedbackUpdateDto {

    @Size(max=500)
    private String interviewFeedback;

    private List<SkillRatingUpdateDto> skillRatings;
}
