package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.RoundResponseDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ApplicationUpdateDto {

    private ApplicationStatusCreateDto applicationStatus;

    private Boolean isShortlisted;

    private List<RoundResponseDto> applicationRounds;

}
