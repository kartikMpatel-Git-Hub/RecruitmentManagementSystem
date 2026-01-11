package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.application;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserMinimalResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortlistedApplicationResponseDto {

    private Integer applicationId;

    private Integer positionId;

    private Integer candidateId;

    private ApplicationStatusResponseDto applicationStatus;

    private Double matchScore;

    private Boolean isShortlisted;

    private UserMinimalResponseDto shortlistedBy;

    private Boolean isSelected;

}

