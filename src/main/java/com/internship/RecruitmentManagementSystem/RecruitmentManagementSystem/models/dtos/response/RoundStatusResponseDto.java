package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoundStatusResponseDto {
    private Integer roundStatusId;

    private RoundStatus roundStatus;

    private String roundFeedback;

    private Double rating;
}
