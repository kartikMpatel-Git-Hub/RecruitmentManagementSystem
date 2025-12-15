package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.application;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.round.RoundResponseDto;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.user.UserMinimalResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponseDto {

    private Integer applicationId;

    private Integer positionId;

    private Integer candidateId;

    private ApplicationStatusResponseDto applicationStatus;

    private Boolean isShortlisted;

    private UserMinimalResponseDto shortlistedBy;

    private Double matchingScore;

    private List<RoundResponseDto> applicationRounds;

}

