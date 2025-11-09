package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response;

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

    private List<RoundResponseDto> applicationRounds;

}

