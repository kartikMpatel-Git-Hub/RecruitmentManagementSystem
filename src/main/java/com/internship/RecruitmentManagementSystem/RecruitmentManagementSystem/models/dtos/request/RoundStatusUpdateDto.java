package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RoundStatusUpdateDto {

    private RoundStatus roundStatus;

    private String roundFeedback;

    private Double rating;

}
