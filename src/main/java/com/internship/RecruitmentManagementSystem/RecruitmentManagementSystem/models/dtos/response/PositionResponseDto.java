package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos.response;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.PositionType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class PositionResponseDto {
    private Integer positionId;

    private String positionTitle;

    private String positionDescription;

    private String positionCriteria;

    private PositionType positionType;

    private Double positionSalary;

    private String positionLocation;

    private String positionLanguage;

    private Integer positionTotalOpening;

    private PositionStatusResponseDto positionStatus;

    private List<PositionRequirementResponseDto> positionRequirements;

    private Integer positionApplications;

    private List<DegreeResponseDto> positionRequiredEducations = new ArrayList<>();

    private UserResponseDto createdBy;

    private List<PositionRoundResponseDto> positionRounds;
}
