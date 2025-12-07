package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.round;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundResult;
import lombok.Data;

@Data
public class RoundResultDto {

    private RoundResult roundResult;

    private String roundFeedback;

    private Double roundRating;
}
