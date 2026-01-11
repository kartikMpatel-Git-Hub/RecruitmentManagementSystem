package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.application;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.round.RoundResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserMinimalResponseDto;
import lombok.Data;

import java.util.Set;

@Data
public class ApplicationRoundResponseDto {
    private Integer applicationId;

    private Integer positionId;

    private Integer candidateId;

    private ApplicationStatusResponseDto applicationStatus;

    private Double matchScore;

    private Boolean isShortlisted;

    private UserMinimalResponseDto shortlistedBy;

    private Boolean isSelected;

    private Set<RoundResponseDto> rounds;
}
