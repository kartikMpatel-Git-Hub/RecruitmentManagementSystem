package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.round.RoundResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class ApplicationDto {

    private Integer applicationId;

    private Integer positionId;

    private Integer candidateId;

    private ApplicationStatusDto applicationStatus;

    private Boolean isShortlisted;

    private List<RoundResponseDto> applicationRounds;

}

