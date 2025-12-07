package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.request.application;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ApplicationCreateDto {

    private Integer positionId;

    private Integer candidateId;

}
