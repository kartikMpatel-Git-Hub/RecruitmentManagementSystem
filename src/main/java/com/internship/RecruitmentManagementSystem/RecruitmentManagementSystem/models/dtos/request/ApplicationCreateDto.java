package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response.RoundResponseDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ApplicationCreateDto {

    private Integer positionId;

    private Integer candidateId;

}
